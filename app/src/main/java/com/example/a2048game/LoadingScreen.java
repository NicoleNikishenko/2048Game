package com.example.a2048game;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import gr.net.maroulis.library.EasySplashScreen;

public class LoadingScreen extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            EasySplashScreen config = new EasySplashScreen(LoadingScreen.this)
                    .withFullScreen()
                    .withTargetActivity(HomeActivity.class)
                    .withBackgroundColor(Color.WHITE)
                    .withLogo(R.drawable.logo2);
            View easySplashScreen = config.create();
            Animation scaleAnim = AnimationUtils.loadAnimation(LoadingScreen.this, R.anim.scale_anim_logo);
            easySplashScreen.startAnimation(scaleAnim);
            setContentView(easySplashScreen);
        }

    @Override
    public void onBackPressed() {

    }
}

