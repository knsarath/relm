package com.test.rlm.activity;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sarath on 14/12/16.
 */
public class Pref {
    private static final String PREFS_NAME = "app_preferrence";
    private static final String DUMMY_DATA_ADDED = "dummy_data_added";


    public static boolean isDummyDataAdded(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(DUMMY_DATA_ADDED, false);
    }

    public static void dummyDataAdded(Context context, boolean b) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DUMMY_DATA_ADDED, b);
        // Commit the edits!
        editor.commit();
    }
}
