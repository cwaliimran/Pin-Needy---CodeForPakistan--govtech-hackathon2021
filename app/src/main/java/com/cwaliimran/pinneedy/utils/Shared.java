package com.cwaliimran.pinneedy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared {
    private static final String PREF_NAME = "PinNeedy";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String value;


    public Shared(Context context) {
        try {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getSharedPref() {
        return sharedPreferences;
    }


    public void clearAllPreferances(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }


    //for int values
    public int setInt(String key, int value) {
        editor = getSharedPref().edit();
        editor.putInt(key, value);
        editor.apply();
        return value;
    }

    public int getInt(String key) {
        return getSharedPref().getInt(key, 0);
    }

    public void setString(String key, String value) {
        try {
            editor = getSharedPref().edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        try {
            value = getSharedPref().getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
