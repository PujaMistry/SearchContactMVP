package com.example.puja.searchcontactmvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.puja.searchcontactmvp.adapter.ContactAdapter;
import com.example.puja.searchcontactmvp.mainview.MainView;
import com.example.puja.searchcontactmvp.network.model.Contact;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MainView, ContactAdapter.ContactSelectedListener {
    private Unbinder unbinder;
    private Presenter presenter;
    private ContactAdapter adapter;

    @BindView(R.id.input_search)
    EditText search;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        presenter = new Presenter(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ContactAdapter(this, this);
        recyclerView.setAdapter(adapter);

        presenter.fetchContacts();
        presenter.searchContact(RxTextView.textChangeEvents(search).skipInitialValue());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshList(List<Contact> contactList) {
        adapter.updateUserList(contactList);
    }

    @Override
    public void onContactSelected(Contact contact) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.dispose();
    }
}
