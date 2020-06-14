package com.example.a2048game.Game;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;

public class Score {

    private static final String TOP_SCORE_PREF = "TopScore pref";
    private static final String SECOND_HIGHEST_SCORE = "2ndHighScore";
    private static final String THIRD_HIGHEST_SCORE = "3rdHighScore";
    private static final String FOURTH_HIGHEST_SCORE = "4thHighScore";
    private static final String FIFTH_HIGHEST_SCORE = "5thHighScore";
    private static final String SIXTH_HIGHEST_SCORE = "6thHighScore";

    private long score, oldTopScore;
    private Long[] leaderBoard;
    private SharedPreferences prefs;
    private Paint paint;

    private boolean newHighScore;

    public Score(Resources resources, Long score , SharedPreferences prefs) {

        this.score = score;
        this.prefs = prefs;
        this.leaderBoard = new Long[6];
        this.oldTopScore = 0;

        leaderBoard[0] = prefs.getLong(TOP_SCORE_PREF,0);
        leaderBoard[1] = prefs.getLong(SECOND_HIGHEST_SCORE,0);
        leaderBoard[2] = prefs.getLong(THIRD_HIGHEST_SCORE,0);
        leaderBoard[3] = prefs.getLong(FOURTH_HIGHEST_SCORE,0);
        leaderBoard[4] = prefs.getLong(FIFTH_HIGHEST_SCORE,0);
        leaderBoard[5] = prefs.getLong(SIXTH_HIGHEST_SCORE,0);


    }

    public void updateScore(Long value){
        score = value;
        checkTopScore();
    }
    public void checkTopScore(){

        if(!newHighScore) {

            leaderBoard[0] = prefs.getLong(TOP_SCORE_PREF, 0);

            if (score > leaderBoard[0]) {

                newHighScore = true;

            }
        }
    }

    public void updateLeaderBoard (){

        prefs.edit().putLong(SIXTH_HIGHEST_SCORE,leaderBoard[4]).apply();
        prefs.edit().putLong(FIFTH_HIGHEST_SCORE,leaderBoard[3]).apply();
        prefs.edit().putLong(FOURTH_HIGHEST_SCORE,leaderBoard[2]).apply();
        prefs.edit().putLong(THIRD_HIGHEST_SCORE,leaderBoard[1]).apply();
        prefs.edit().putLong(SECOND_HIGHEST_SCORE,leaderBoard[0]).apply();
        prefs.edit().putLong(TOP_SCORE_PREF,score).apply();
        refreshLeaderBoard();

    }

    public void refreshLeaderBoard (){

        leaderBoard[0] = prefs.getLong(TOP_SCORE_PREF,0);
        leaderBoard[1] = prefs.getLong(SECOND_HIGHEST_SCORE,0);
        leaderBoard[2] = prefs.getLong(THIRD_HIGHEST_SCORE,0);
        leaderBoard[3] = prefs.getLong(FOURTH_HIGHEST_SCORE,0);
        leaderBoard[4] = prefs.getLong(FIFTH_HIGHEST_SCORE,0);
        leaderBoard[5] = prefs.getLong(SIXTH_HIGHEST_SCORE,0);

    }

    public void checkIfNewMidScore(){

        boolean found = false;

        for(int i = 1; i < 6; i++){

            if(!found) {
                if (score > leaderBoard[i]) {
                    switch (i) {

                        case 1:
                            prefs.edit().putLong(SIXTH_HIGHEST_SCORE, leaderBoard[4]).apply();
                            prefs.edit().putLong(FIFTH_HIGHEST_SCORE, leaderBoard[3]).apply();
                            prefs.edit().putLong(FOURTH_HIGHEST_SCORE, leaderBoard[2]).apply();
                            prefs.edit().putLong(THIRD_HIGHEST_SCORE, leaderBoard[1]).apply();
                            prefs.edit().putLong(SECOND_HIGHEST_SCORE, score).apply();
                            found = true;
                            break;

                        case 2:
                            prefs.edit().putLong(SIXTH_HIGHEST_SCORE, leaderBoard[4]).apply();
                            prefs.edit().putLong(FIFTH_HIGHEST_SCORE, leaderBoard[3]).apply();
                            prefs.edit().putLong(FOURTH_HIGHEST_SCORE, leaderBoard[2]).apply();
                            prefs.edit().putLong(THIRD_HIGHEST_SCORE, score).apply();
                            found = true;
                            break;

                        case 3:
                            prefs.edit().putLong(SIXTH_HIGHEST_SCORE, leaderBoard[4]).apply();
                            prefs.edit().putLong(FIFTH_HIGHEST_SCORE, leaderBoard[3]).apply();
                            prefs.edit().putLong(FOURTH_HIGHEST_SCORE, score).apply();
                            found = true;
                            break;

                        case 4:
                            prefs.edit().putLong(SIXTH_HIGHEST_SCORE, leaderBoard[4]).apply();
                            prefs.edit().putLong(FIFTH_HIGHEST_SCORE, score).apply();
                            found = true;
                            break;

                        case 5:
                            prefs.edit().putLong(SIXTH_HIGHEST_SCORE, score).apply();
                            found = true;
                            break;


                    }

                }
            }
        }

    }

    public Long getScore(){ return score;}
    public Long getTopScore(){ return leaderBoard[0];}
    public boolean isNewHighScore(){ return newHighScore;}


    public void setTopScore(Long value){

        prefs.edit().putLong(TOP_SCORE_PREF,value).apply();
    }

}

