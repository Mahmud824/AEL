package com.example.xyz;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.xyz.Prefs.UserPrefs;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        //Checking if it is first time--------------------------------------------------------------
        final UserPrefs prefs = new UserPrefs(SplashActivity.this);
        if (!prefs.getIsFirstTime()){
            Intent intent = new Intent(SplashActivity.this,MiddleActivity.class);
            startActivity(intent);
            finish();

        } else {
            setContentView(R.layout.activity_splash);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this,MiddleActivity.class);
                    startActivity(intent);
                    prefs.setIsFirstTime(false);
                    finish();
                }
            },SPLASH_TIME_OUT);
        }



    }
}