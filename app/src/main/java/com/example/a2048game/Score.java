package com.example.a2048game;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Score {

    private static final String TOP_SCORE_PREF = "TopScore pref";

    private int score, topScore;
    private SharedPreferences prefs;
    private Paint paint;

    public Score(Resources resources, int score , SharedPreferences prefs) {

        this.score = score;
        this.prefs = prefs;


        topScore = prefs.getInt(TOP_SCORE_PREF,0);


    }

    public void updateScore(int value){

        score += value;
        checkTopScore();
    }

    public void checkTopScore(){

        topScore = prefs.getInt(TOP_SCORE_PREF,0);

        if(score > topScore){
            prefs.edit().putInt(TOP_SCORE_PREF,score).apply();
            topScore = score;
        }
    }

    public int getScore(){ return score;}
    public int getTopScore(){ return topScore;}


    public void setTopScore(int value){

        prefs.edit().putInt(TOP_SCORE_PREF,value).apply();
    }

}

