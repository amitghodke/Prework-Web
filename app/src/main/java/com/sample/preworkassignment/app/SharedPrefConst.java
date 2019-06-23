package com.sample.preworkassignment.app;

import
        android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefConst {

    public static final String SharedPref = "SharedPref";
    static SharedPrefConst instance;
    SharedPreferences sharedPreferences;
    Editor editor;

    public SharedPrefConst(Context context) {
        sharedPreferences = context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
    }

    public static SharedPrefConst getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefConst(context);
        }
        return instance;
    }

    public void setIsLoggedIn(boolean islogin) {
        editor = sharedPreferences.edit();
        editor.putBoolean("islogin", islogin);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("islogin", false);
    }


    public void clearSharedPref() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void addString(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "'");
    }

}
