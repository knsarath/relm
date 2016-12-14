package com.test.rlm.app;

import android.app.Application;

import com.test.rlm.realm.RealmController;

public class MyApplication extends Application {

    private static final Integer DB_VERSION = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmController.init(this, DB_VERSION);
    }
}