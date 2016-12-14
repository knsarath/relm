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

    //clear all objects from given realm class(Table)
    public void clearAll(Class<? extends RealmObject> clazz) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
        realm.close();
    }

    //find all objects in the Book.class
    public RealmResults<Book> getBooks() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Book.class).findAll();
    }


    //check if Book.class is empty
    public boolean hasBooks(Class<? extends RealmObject> clazz) {
        Realm realm = Realm.getDefaultInstance();
        return !realm.where(clazz).findAll().isEmpty();
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

    public void saveAll(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
    }

    public void saveOrUpdate(RealmObject book) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(book);
        realm.commitTransaction();
        realm.close();
    }

    public void saveAllOrUpdate(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
    }

    public void delete(Class<? extends RealmObject> aClass, int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(aClass).findAll().deleteFromRealm(position);
        realm.commitTransaction();
    }

    public <E extends RealmObject> E get(Class<E> type, int position) {
        E e = null;
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> realmResults = realm.where(type).findAll();
        final E result = realmResults.get(position);
        if (result != null) {
            e = realm.copyFromRealm(result);
        }
        realm.close();
        return e;
    }
}
