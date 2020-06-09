package com.example.a2048game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView scoreTv;
    private TextView topScoreTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        this.scoreTv = findViewById(R.id.score_value);
        this.topScoreTv = findViewById(R.id.topscore_value);

    }


    public static Context getContext(){
        return mContext;
    }

    public void updateScore(final int score, final int topScore){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTv.setText(Integer.toString(score));
                topScoreTv.setText(Integer.toString(topScore));
            }
        });

    }


    }



