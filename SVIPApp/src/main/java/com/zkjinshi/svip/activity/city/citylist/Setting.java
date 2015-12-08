package com.zkjinshi.svip.activity.city.citylist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Setting {

    public static String LoadFromSharedPreferences(Context context, String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString(key, "");
    }

    public static void Save2SharedPreferences(Context context, String key, String value) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor ed = mPrefs.edit();
        ed.putString(key, value);
        ed.commit();
    }
}
