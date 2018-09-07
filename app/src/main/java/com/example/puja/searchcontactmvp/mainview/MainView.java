package com.example.puja.searchcontactmvp.mainview;

import com.example.puja.searchcontactmvp.network.model.Contact;

import java.util.List;

public interface MainView {

    void refreshList(List<Contact> contactList);
    void onContactSelected(Contact contact);
}
