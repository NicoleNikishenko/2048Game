package com.example.a2048game;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView scoreTv;
    private TextView topScoreTv;
    Dialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        this.scoreTv = findViewById(R.id.tv_current_score);
        this.topScoreTv = findViewById(R.id.tv_best_score);

        ImageButton btnNewGame = findViewById(R.id.btn_home);
        btnNewGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


    }


    public static Context getContext(){
        return mContext;
    }

    public void updateScore(final int score, final int topScore){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(score == 0) {
                    scoreTv.setText("START!");
                } else {
                    scoreTv.setText(Integer.toString(score));
                }
                topScoreTv.setText(Integer.toString(topScore));
            }
        });

    }

}






