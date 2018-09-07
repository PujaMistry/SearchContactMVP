package com.example.puja.searchcontactmvp;

import android.content.Context;
import android.util.Log;

import com.example.puja.searchcontactmvp.adapter.ContactAdapter;
import com.example.puja.searchcontactmvp.mainview.MainView;
import com.example.puja.searchcontactmvp.network.ApiClient;
import com.example.puja.searchcontactmvp.network.ApiService;
import com.example.puja.searchcontactmvp.network.model.Contact;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Presenter {

    private MainView mainView;
    private CompositeDisposable disposable = new CompositeDisposable();
    private PublishSubject<String> publishSubject = PublishSubject.create();
    private ApiService apiService;

    public Presenter(MainView mainView) {
        this.mainView = mainView;
        apiService = ApiClient.getClient().create(ApiService.class);
        getContacts();
    }

    public void fetchContacts() {
        publishSubject.onNext("");
    }

    private void getContacts() {
        disposable.add(publishSubject
                .distinctUntilChanged()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMapSingle(new Function<String, Single<List<Contact>>>() {
                    @Override
                    public Single<List<Contact>> apply(String s) throws Exception {
                        return apiService.getContacts(null, s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeWith(getObserver()));
    }

    private DisposableObserver<List<Contact>> getObserver() {
        return new DisposableObserver<List<Contact>>() {
            @Override
            public void onNext(List<Contact> contacts) {
                mainView.refreshList(contacts);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("TAG", "OnError " + e.getMessage().toString());
            }

            @Override
            public void onComplete() {

            }
        };

    }

    public void searchContact(Observable<TextViewTextChangeEvent> charSequenceObservable) {
        charSequenceObservable
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getSearchObserver());
    }

    private DisposableObserver<TextViewTextChangeEvent> getSearchObserver() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                publishSubject.onNext(textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void dispose() {
        disposable.dispose();
    }
}
