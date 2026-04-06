package com.manida.datecounter;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {
    private static final String SP_NAME = "date-counter";
    public static final String TV1 = "First Person";
    public static final String TV2 = "Second Person";
    public static final String IV1 = "Image 1";
    public static final String IV2 = "Image 2";
    public static final String DATE = "date";
    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
    public static void saveLong(Context context, String key, long value) {
        var sp = getSP(context);
        var editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key) {
        var sp = getSP(context);
        return sp.getLong(key, -1);
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sp = getSP(context);
        SharedPreferences.Editor editor = sp.edit(); // to save
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) { // male
        SharedPreferences sp = getSP(context);
        return sp.getString(key, key);
    }
}
