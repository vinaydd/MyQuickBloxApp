package com.hp.myquickbloxapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 2/10/15.
 */
public class AppPreference {
    private final SharedPreferences appSharedPrefs;
    private final SharedPreferences.Editor prefsEditor;

    public AppPreference(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(Constants.APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();

    }

    public String getStringValueForTag(String tagName) {
        return appSharedPrefs.getString(tagName, "");

    }

    public void setStringValueForTag(String tagName, String value) {
        prefsEditor.putString(tagName, value);
        prefsEditor.commit();
    }

    public void setIntValueForTag(String tagName, int value) {
        prefsEditor.putInt(tagName, value);
        prefsEditor.commit();
    }

    public int getIntValueForTag(String tagName) {
        return appSharedPrefs.getInt(tagName, 0);
    }

    public void setBooleanValueForTag(String tagName, boolean value) {
        prefsEditor.putBoolean(tagName, value);
        prefsEditor.commit();
    }

    public boolean getBooleanValueForTag(String tagName) {
        return appSharedPrefs.getBoolean(tagName, false);
    }

    public void clearPreferences() {
        prefsEditor.clear();
        prefsEditor.commit();
    }


}