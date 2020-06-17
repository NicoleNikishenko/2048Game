package com.example.a2048game.Game;
import android.content.SharedPreferences;


public class Score {


    private static final String TOP_SCORE_PREF = "TopScore pref";

    private long score;
    private Long ScoreBoard;
    private SharedPreferences prefs;
    private String rowsString;
    private String colsString;
    private String gameModeString;


    private boolean newHighScore;

    public Score(Long score , SharedPreferences prefs, int gameMode, int rows, int cols) {

        this.score = score;
        this.prefs = prefs;
        this.gameModeString = Integer.toString(gameMode);
        this.rowsString = Integer.toString(rows);
        this.colsString = Integer.toString(cols);
        this.ScoreBoard = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString,0);
    }

    public void updateScore(Long value){
        score = value;
        checkTopScore();
    }
    public void checkTopScore(){
        if(!newHighScore) {
            ScoreBoard = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString, 0);
            if (score > ScoreBoard) {
                newHighScore = true;
            }
        }
    }

    public void updateScoreBoard (){
        prefs.edit().putLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString,score).apply();
    }

    public void refreshScoreBoard (){
        ScoreBoard = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString,0);
    }

    public Long getScore(){ return score;}
    public Long getTopScore(){ return ScoreBoard;}
    public boolean isNewHighScore(){ return newHighScore;}

}

