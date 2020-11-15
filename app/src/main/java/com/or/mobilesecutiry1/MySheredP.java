package com.or.mobilesecutiry1;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class MySheredP {
    private SharedPreferences prefs;

    public MySheredP(Context context) {
         prefs = context.getSharedPreferences("MyPref1", MODE_PRIVATE);
    }


    public String getString(String key, String defValue)
    {
       return prefs.getString(key  , defValue);
    }

    public void putString(String key, String value)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue)
    {
        return prefs.getInt(key  , defValue);
    }


    public void putInt(String key, int value)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public void removeKey(String key)
    {
        prefs.edit().remove(key);
    }


    public boolean isValid()
    {
        if(!prefs.contains("initialized"))
            return true;
        return false;
    }
}
