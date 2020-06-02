package com.example.a2048game;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
    }
    public static Context getContext(){
        return mContext;
    }
}
