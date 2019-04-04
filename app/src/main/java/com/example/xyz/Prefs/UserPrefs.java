package com.example.xyz.Prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public final class UserPrefs {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String IS_FIRST_TIME = "is_first_time";

    @SuppressLint("CommitPrefEdits")
    public UserPrefs(Context context) {
        preferences = context.getSharedPreferences("my_pref",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setIsFirstTime(boolean status){
        editor.putBoolean(IS_FIRST_TIME, status);
        editor.commit();
    }

    public boolean getIsFirstTime(){
        return preferences.getBoolean(IS_FIRST_TIME, true);
    }
}
