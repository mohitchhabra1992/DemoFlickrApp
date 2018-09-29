package com.example.mohit.demoflickrsearch;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    static final String FLICKR_QUERY = "FLICKR_QUERY";
    static final String PHOTO_TRANSFER ="PHOTO_TRANSFER";
    static final String COLUMNS ="COLUMNS";


    void activateToolbar(boolean enableHome){
        Log.d(TAG, "activateToolbar: starts");
        ActionBar actionBar = getSupportActionBar();
        if( actionBar == null){
            Toolbar toolbar = findViewById(R.id.toolbar);

            if(toolbar != null){
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }
        if( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }
}
