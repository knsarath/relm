package com.test.rlm.realm;


import android.content.Context;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmController {


    /**
     * inititallize the realm . Do it in the application launch
     *
     * @param context
     * @param dbVersion
     */
    public static void init(Context context, Integer dbVersion) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(dbVersion)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    public interface Callback<E> {
        void onSuccess(E result);

        void onError();
    }

    public interface WriteCallback {
        void onSuccess();

        void onError(Throwable error);
    }


    //==========================READS ============================//


    /**
     * read all objects from a table. It returns an in-memory copy of results , which is detached from the realm instance.
     *
     * @param clazz
     * @param <E>
     * @return
     */
    public <E extends RealmObject> List<E> getAll(Class<E> clazz) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> realmObjects = realm.where(clazz).findAll();
        final List<E> list = realm.copyFromRealm(realmObjects);
        realm.close();
        return list;
    }

    /**
     * read all objects from a table asynchronously (in separate thread). It returns an in-memory copy of results , which is detached from the realm instance.
     *
     * @param clazz
     * @param listCallback
     * @param <E>
     */
    public <E extends RealmObject> void getAllAsync(final Class<E> clazz, final Callback<List<E>> listCallback) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> realmResults = realm.where(clazz).findAllAsync();
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<E>>() {
            @Override
            public void onChange(RealmResults<E> results) {
                if (results.isLoaded()) { // isLoaded is true when the query is completed and all results are available
                    final List<E> copyFromRealm = realm.copyFromRealm(results);
                    realm.close();
                    if (listCallback != null) {
                        listCallback.onSuccess(copyFromRealm);
                    }
                }
            }
        });

    }


    /***
     * //find all objects in the given table. Note: result is not detached from realm.
     * So it returns a actual result set from realm with a valid realm instance associated with it
     *
     * @param clazz class which extends RealmObject
     * @return RealmResults of clazz.
     */
    public <E extends RealmObject> RealmResults<E> getAllRealm(Class<E> clazz) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(clazz).findAll();

    }

    /**
     * find all objects in the given table asynchronously. Note: result is not detached from realm.
     * So it returns a actual result set from realm with a valid realm instance associated with it
     *
     * @param clazz
     * @param listCallback
     * @param <E>
     */
    public <E extends RealmObject> void getAllRealmAsync(final Class<E> clazz, final Callback<RealmResults<E>> listCallback) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> realmResults = realm.where(clazz).findAllAsync();
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<E>>() {
            @Override
            public void onChange(RealmResults<E> results) {
                if (results.isLoaded()) { // isLoaded is true when the query is completed and all results are available
                    if (listCallback != null) {
                        listCallback.onSuccess(results);
                    }
                }
            }
        });

    }


    /**
     * find all objects in the given table sorted with a given field. Note: The returned result set is an in  memory copy of the actual result.
     * so any further update will not immediately affect the actual realm data until you do a commit
     *
     * @param clazz
     * @param fieldName
     * @param sortOrder
     * @param <E>
     * @return
     */
    public <E extends RealmObject> RealmList<E> getAllSorted(Class<E> clazz, String fieldName, Sort sortOrder) {
        Realm realm = Realm.getDefaultInstance();
        final List<E> list = realm.copyFromRealm(realm.where(clazz).findAllSorted(fieldName, sortOrder));
        realm.close();
        RealmList<E> eRealmList = new RealmList<>();
        eRealmList.addAll(list);
        return eRealmList;
    }

    /**
     * find all objects in the given table sorted with a given field. This will be executed in background thread
     * Note: The returned result set is an in  memory copy of the actual result.
     * so any further update will not immediately affect the actual realm data until you do a commit
     *
     * @param clazz
     * @param fieldName
     * @param sortOrder
     * @param callback  {@link Callback} to return the results
     * @param <E>
     */
    public <E extends RealmObject> void getAllSortedAsync(Class<E> clazz, String fieldName, Sort sortOrder, final Callback<RealmList<E>> callback) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> queryResult = realm.where(clazz).findAllSortedAsync(fieldName, sortOrder);
        queryResult.addChangeListener(new RealmChangeListener<RealmResults<E>>() {
            @Override
            public void onChange(RealmResults<E> results) {
                if (results.isLoaded()) {
                    realm.close();
                    RealmList<E> finalResult = new RealmList<>();
                    finalResult.addAll(realm.copyFromRealm(results));
                    if (callback != null) {
                        callback.onSuccess(finalResult);
                    }
                }
            }
        });

    }


    /**
     * This method will return sorted result with a field.
     * Note: the returned result set is not detached from realm so , the active realm instance is associated with the result set.
     * any modification will affect the actual realm objects.
     *
     * @param clazz
     * @param fieldName
     * @param sortOrder
     * @param <E>       class which extends RealmObject (A class representing a realm table)
     * @return
     */
    public <E extends RealmObject> RealmResults<E> getAllSortedRealm(Class<E> clazz, String fieldName, Sort sortOrder) {
        Realm realm = Realm.getDefaultInstance();
        /**
         * Note : the realm instance is not closed here
         */
        return realm.where(clazz).findAllSorted(fieldName, sortOrder);
    }

    /**
     * This method will return sorted result with a field . This will run in a background thread
     * Note: the returned result set is not detached from realm so , the active realm instance is associated with the result set.
     * any modification will affect the actual realm objects.
     *
     * @param clazz
     * @param fieldName
     * @param sortOrder
     * @param callback  {@link Callback} to return the results
     * @param <E>
     */
    public <E extends RealmObject> void getAllSortedRealmAsync(Class<E> clazz, String fieldName, Sort sortOrder, final Callback<RealmResults<E>> callback) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<E> allSortedAsync = realm.where(clazz).findAllSortedAsync(fieldName, sortOrder);
        allSortedAsync.addChangeListener(new RealmChangeListener<RealmResults<E>>() {
            @Override
            public void onChange(RealmResults<E> element) {
                if (element.isLoaded()) {
                    if (callback != null) {
                        callback.onSuccess(element);
                    }
                }
            }
        });
    }


    /**
     * this will return a realm object by position and will return an in-memory copy of the result object
     *
     * @param type
     * @param position
     * @param <E>
     * @return
     */
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


    //=======================  WRITES ======================

    /**
     * save a single object to realm. will throw error if primary key already present
     *
     * @param object
     */
    public <E extends RealmObject> void save(E object) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * save a single object to realm.
     * This will be executed in background thread
     * will throw error if primary key already present
     *
     * @param object
     * @param callback {{@link WriteCallback having success and failure callbacks}}
     * @param <E>
     */
    public <E extends RealmObject> void saveAsync(final E object, final WriteCallback callback) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(error);
                }
                realm.close();
            }
        });
    }


    /**
     * save a list of objects to realm. will throw error if primary key already present
     *
     * @param items
     */
    public void saveAll(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
    }

    /**
     * save a list of objects to realm.
     * This will be executed in separate thread
     * will throw error if primary key already present
     *
     * @param items
     * @param callback {{@link WriteCallback to get notified when write completes }}
     */
    public void saveAllAsync(final Iterable<? extends RealmObject> items, final WriteCallback callback) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(items);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(error);
                }
                realm.close();
            }
        });
    }


    /**
     * save a single of realm object. if the primary key field is already present , it will update all other fields of existing object
     *
     * @param book
     */
    public void saveOrUpdate(RealmObject book) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(book);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * save a list of realm objects. if the primary key field is already present , it will update the existing object (it will update all other fields)
     *
     * @param items
     */
    public void saveAllOrUpdate(Iterable<? extends RealmObject> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
    }

    /**
     * save a list of realm objects. if the primary key field is already present ,
     * This will be executed in separate thread
     * it will update the existing object (it will update all other fields)
     * @param items
     * @param callback
     */
    public void saveAllOrUpdateAsync(final Iterable<? extends RealmObject> items, final WriteCallback callback) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(items);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(error);
                }
                realm.close();
            }
        });
    }


    /**
     * deletes a single object from realm by position
     *
     * @param aClass
     * @param position
     */
    public void delete(Class<? extends RealmObject> aClass, int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(aClass).findAll().deleteFromRealm(position);
        realm.commitTransaction();
    }


    /**
     * clear all objects from given realm class(Table)
     */
    public void clearAll(Class<? extends RealmObject> clazz) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
        realm.close();
    }
}
