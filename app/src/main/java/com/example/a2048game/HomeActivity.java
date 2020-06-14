package com.example.a2048game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        Animation scaleAnim = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.scale_anim);

        Button btnNewGame = findViewById(R.id.btn_new_game);
        Button btnLoadGame = findViewById(R.id.btn_load_game);
        Button brnSettings = findViewById(R.id.btn_settings);
        Button btnLeaderBoard = findViewById(R.id.btn_leader_board);
        btnNewGame.startAnimation(scaleAnim);
        btnLoadGame.startAnimation(scaleAnim);
        brnSettings.startAnimation(scaleAnim);
        btnLeaderBoard.startAnimation(scaleAnim);
        btnLoadGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CustomGameActivity.class);
                startActivity(intent);
            }
        });
    }
}
