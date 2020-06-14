package com.example.a2048game;

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
    private static final int GAME_MODE_CLASSIC = 0;
    private static final int GAME_MODE_SHUFFLE = 1;
    private static final int GAME_MODE_SOLID_TILE = 2;

    private int score, oldTopScore;
    private int[] leaderBoard;
    private SharedPreferences prefs;
    private Paint paint;
    private int gameMode;
    private String gameModeString;

    private boolean newHighScore;

    public Score(Resources resources, int score , SharedPreferences prefs, int gameMode) {

        this.score = score;
        this.prefs = prefs;
        this.leaderBoard = new int[6];
        this.oldTopScore = 0;
        this.gameMode = gameMode;
        this.gameModeString = Integer.toString(gameMode);

        leaderBoard[0] = prefs.getInt(TOP_SCORE_PREF + gameModeString,0);
        leaderBoard[1] = prefs.getInt(SECOND_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[2] = prefs.getInt(THIRD_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[3] = prefs.getInt(FOURTH_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[4] = prefs.getInt(FIFTH_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[5] = prefs.getInt(SIXTH_HIGHEST_SCORE + gameModeString,0);


    }

    public void updateScore(int value){
        score = value;
        checkTopScore();
    }
    public void checkTopScore(){

        if(!newHighScore) {

            leaderBoard[0] = prefs.getInt(TOP_SCORE_PREF + gameModeString, 0);

            if (score > leaderBoard[0]) {

                newHighScore = true;

            }
        }
    }

    public void updateLeaderBoard (){

        prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString,leaderBoard[4]).apply();
        prefs.edit().putInt(FIFTH_HIGHEST_SCORE + gameModeString,leaderBoard[3]).apply();
        prefs.edit().putInt(FOURTH_HIGHEST_SCORE + gameModeString,leaderBoard[2]).apply();
        prefs.edit().putInt(THIRD_HIGHEST_SCORE + gameModeString,leaderBoard[1]).apply();
        prefs.edit().putInt(SECOND_HIGHEST_SCORE + gameModeString,leaderBoard[0]).apply();
        prefs.edit().putInt(TOP_SCORE_PREF + gameModeString,score).apply();
        refreshLeaderBoard();

    }

    public void refreshLeaderBoard (){

        leaderBoard[0] = prefs.getInt(TOP_SCORE_PREF + gameModeString,0);
        leaderBoard[1] = prefs.getInt(SECOND_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[2] = prefs.getInt(THIRD_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[3] = prefs.getInt(FOURTH_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[4] = prefs.getInt(FIFTH_HIGHEST_SCORE + gameModeString,0);
        leaderBoard[5] = prefs.getInt(SIXTH_HIGHEST_SCORE + gameModeString,0);

    }

    public void checkIfNewMidScore(){

        boolean found = false;

        for(int i = 1; i<6; i++){

            if(!found) {
                if (score > leaderBoard[i]) {
                    switch (i) {

                        case 1:
                            prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString, leaderBoard[4]).apply();
                            prefs.edit().putInt(FIFTH_HIGHEST_SCORE + gameModeString, leaderBoard[3]).apply();
                            prefs.edit().putInt(FOURTH_HIGHEST_SCORE + gameModeString, leaderBoard[2]).apply();
                            prefs.edit().putInt(THIRD_HIGHEST_SCORE + gameModeString, leaderBoard[1]).apply();
                            prefs.edit().putInt(SECOND_HIGHEST_SCORE + gameModeString, score).apply();
                            found = true;
                            break;

                        case 2:
                            prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString, leaderBoard[4]).apply();
                            prefs.edit().putInt(FIFTH_HIGHEST_SCORE + gameModeString, leaderBoard[3]).apply();
                            prefs.edit().putInt(FOURTH_HIGHEST_SCORE + gameModeString, leaderBoard[2]).apply();
                            prefs.edit().putInt(THIRD_HIGHEST_SCORE + gameModeString, score).apply();
                            found = true;
                            break;

                        case 3:
                            prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString, leaderBoard[4]).apply();
                            prefs.edit().putInt(FIFTH_HIGHEST_SCORE + gameModeString, leaderBoard[3]).apply();
                            prefs.edit().putInt(FOURTH_HIGHEST_SCORE + gameModeString, score).apply();
                            found = true;
                            break;

                        case 4:
                            prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString, leaderBoard[4]).apply();
                            prefs.edit().putInt(FIFTH_HIGHEST_SCORE + gameModeString, score).apply();
                            found = true;
                            break;

                        case 5:
                            prefs.edit().putInt(SIXTH_HIGHEST_SCORE + gameModeString, score).apply();
                            found = true;
                            break;


                    }

                }
            }
        }

    }

    public int getScore(){ return score;}
    public int getTopScore(){ return leaderBoard[0];}
    public boolean isNewHighScore(){ return newHighScore;}


    public void setTopScore(int value){

        prefs.edit().putInt(TOP_SCORE_PREF + gameModeString,value).apply();
    }

}

