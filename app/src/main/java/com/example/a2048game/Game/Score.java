package com.example.a2048game.Game;
import android.content.SharedPreferences;


public class Score {


    private static final String TOP_SCORE_PREF = "TopScore pref";

    private long score, prevScore;
    private Long TopScore, prevTopScore;
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
        this.TopScore = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString,0);
        this.prevTopScore = TopScore;
    }

    public void updateScore(Long value){
        prevScore = score;
        score = value;
        if(!newHighScore)
            checkTopScore();
        else{
            prevTopScore = TopScore;
            TopScore = score;
        }

    }
    public void checkTopScore(){
        if(!newHighScore) {
            TopScore = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString, 0);
            if (score > TopScore) {
                newHighScore = true;
                prevTopScore = TopScore;
                TopScore = score;
            }
        }
    }

    public void updateScoreBoard (){

        prefs.edit().putLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString, TopScore).apply();
    }

    public void refreshScoreBoard (){
        TopScore = prefs.getLong(TOP_SCORE_PREF + gameModeString + rowsString + colsString,0);
    }

    public Long getScore(){ return score;}
    public Long getTopScore(){ return TopScore;}
    public boolean isNewHighScore(){ return newHighScore;}
    public void resetGame(){ score = 0; refreshScoreBoard(); newHighScore = false; }

    public void undoScore(){
        score = prevScore;
        TopScore = prevTopScore;
        if(score < TopScore) {
            newHighScore = false;
            updateScoreBoard();
        }
    }

}

