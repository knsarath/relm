package com.test.rlm.realm;


import android.content.Context;

import com.test.rlm.model.Book;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class RealmController {


    public static void init(Context context, Integer dbVersion) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(dbVersion)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    //clear all objects from Book.class
    public void clearAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Book.class);
        realm.commitTransaction();
        realm.close();
    }

    //find all objects in the Book.class
    public RealmResults<Book> getBooks() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Book.class).findAll();
    }

    //query a single item with the given id
    public Book getBook(String id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Book.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasBooks() {
        Realm realm = Realm.getDefaultInstance();
        return !realm.where(Book.class).findAll().isEmpty();
    }

    //query example
    public RealmResults<Book> queryedBooks() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Book.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }

    public void save(RealmObject book) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(book);
        realm.commitTransaction();
        realm.close();
    }

    public void saveOrUpdate(RealmObject book) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(book);
        realm.commitTransaction();
        realm.close();
    }

    public void saveAll(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
    }

    public void saveAllOrUpdate(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
    }


}
