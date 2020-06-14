package com.example.a2048game.Game;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Score {

    private static final String TOP_SCORE_PREF = "TopScore pref";

    private long score, topScore;
    private SharedPreferences prefs;


    public Score(Resources resources, Long score , SharedPreferences prefs) {
        this.score = score;
        this.prefs = prefs;
        topScore = prefs.getLong(TOP_SCORE_PREF,0);
    }

    public void updateScore(long value){
        score = value;
        checkTopScore();
    }
    public void checkTopScore(){

        topScore = prefs.getLong(TOP_SCORE_PREF,0);

        if(score > topScore){
            prefs.edit().putLong(TOP_SCORE_PREF,score).apply();
            topScore = score;
        }
    }

    public long getScore(){ return score;}
    public long getTopScore(){ return topScore;}


}

