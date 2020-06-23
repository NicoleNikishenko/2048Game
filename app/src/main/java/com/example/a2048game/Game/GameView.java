package com.example.a2048game.Game;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.a2048game.GameActivity;
import com.example.a2048game.R;
import com.example.a2048game.Tiles.BitmapCreator;

import java.util.Objects;


public class GameView  extends SurfaceView  implements SurfaceHolder.Callback{

    private static final String APP_NAME = "2048Project";


    private MediaPlayer swipe;
    private MainThread thread;
    private boolean isInit;
    private boolean isTutorial;
    private boolean isTutorialFromMainScreen;
    private boolean isWinningMsgPlayed;
    private boolean isNewScoreMsgPlayed;

    private Score score;
    GameBoard gameBoard;
    Boolean dialogOpen = false;
    Drawable backgroundRectangle = getResources().getDrawable(R.drawable.gameboard_background);
    Drawable cellRectangle = getResources().getDrawable(R.drawable.gameboard_cell_shape);
    AlertDialog.Builder builder;
    GameActivity gameActivity;
    private Dialog gameOverDialog;





    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);


        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.gameActivity = (GameActivity)context;
        swipe = MediaPlayer.create(gameActivity, R.raw.swipe);

        isInit = false;
        int exponent = gameActivity.getBoardExponent();
        int rows = gameActivity.getBoardRows();
        int cols = gameActivity.getBoardCols();
        int gameMode = gameActivity.getGameMode();

        isTutorial = gameActivity.isTutorial();
        isTutorialFromMainScreen = gameActivity.isTutorialFromMainScreen();


        this.score = new Score((long)0, getContext().getSharedPreferences(APP_NAME,Context.MODE_PRIVATE),gameMode, rows, cols);


        gameBoard = new GameBoard(rows, cols, exponent, this ,gameMode);
        BitmapCreator.exponent = exponent;

        builder = new AlertDialog.Builder(GameActivity.getContext());
        gameOverDialog = new Dialog(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(holder, this);
        gameActivity.setThread(thread);
        thread.setRunning(true);
        thread.start();


        //Initializing board
        gameActivity.updateScore(score.getScore(),score.getTopScore());
        initBarButtons();
        initSwipeListener();
        prepareGameOverDialog();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceHolder(holder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                BitmapCreator bitmapCreator = new BitmapCreator();
                bitmapCreator.clearBitmapArray();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {
        if(gameBoard.isGameOver() && !dialogOpen){
            dialogOpen = true;
            showGameOverDialog();
        }

        if(score.isNewHighScore()){
            //if we have a new highscore we will update the  Scoreboard. else we will check if we got a new midscore and if so we will update the Scoreboard appropriately
            score.updateScoreBoard();
            score.refreshScoreBoard();

//            if (!isNewScoreMsgPlayed) {
//                showAnnouncingMsg(getResources().getString(R.string.new_score));
//                isNewScoreMsgPlayed = true;
//            }
        }
        if (!isWinningMsgPlayed && gameBoard.isGameWon()){
            showAnnouncingMsg(getResources().getString(R.string.winner));
            isWinningMsgPlayed = true;
        }
        if(isInit) {
            gameBoard.update();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());
        drawEmptyBoard(canvas);
        if (!isInit){
            if (isTutorial){
                firstTutorialScreen();
                gameBoard.initTutorialBoard();
            } else {
                gameBoard.initBoard();
            }
        } isInit = true;
        gameBoard.draw(canvas);
    }

    public void updateScore(long value){
        score.updateScore(value);
        gameActivity.updateScore(score.getScore(),score.getTopScore());

    }

    private void drawEmptyBoard (Canvas canvas){
        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());

        //adding padding to frame


        int padding = (int) pxFromDp(3);
        int width = getWidth() - padding * 2;
        int height = getHeight() - padding * 2;

        //calculating cell size
        int cellWidth = width / gameBoard.getCols();
        int cellHeight = height / gameBoard.getRows();

        BitmapCreator.cellDefaultHeight = cellHeight;
        BitmapCreator.cellDefaultWidth = cellWidth;

        //drawing empty tiles
        for (int x = 0; x < gameBoard.getCols(); x++) {
            for (int y = 0; y < gameBoard.getRows(); y++) {

                //start cell position
                int posX = x * cellWidth + padding;
                int posY = y * cellHeight + padding;

                //end cell position
                int posXX = posX + cellWidth;
                int posYY = posY + cellHeight;


                cellRectangle.setColorFilter(getResources().getColor(R.color.valueEmpty), PorterDuff.Mode.SRC_OVER);
                drawDrawable(canvas, cellRectangle, posX, posY, posXX, posYY);

                //filling positions Matrix only on init
                if (!isInit) {
                    gameBoard.setPositions(y, x, posX, posY);
                }
            }
        }
    }
    private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    private void initSwipeListener() {
        setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
            public void onSwipeTop() { if(!dialogOpen) { gameBoard.up(); secondTutorialScreen();}}
            public void onSwipeRight() { if(!dialogOpen) { gameBoard.right(); secondTutorialScreen();}}
            public void onSwipeLeft() { if(!dialogOpen) { gameBoard.left(); secondTutorialScreen();}}
            public void onSwipeBottom() { if(!dialogOpen) { gameBoard.down(); secondTutorialScreen();}}
        });
    }
    private void initBarButtons() {
        ImageButton resetBtn = gameActivity.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isTutorial) {
                    playClick();
                    if (score.isNewHighScore())
                        score.updateScoreBoard();
                    score.refreshScoreBoard();
                    gameBoard.resetGame();
                    isNewScoreMsgPlayed = false;
                    isWinningMsgPlayed = false;
                }

            }
        });

        ImageButton undoBtn = gameActivity.findViewById(R.id.btn_undo);
        undoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isTutorial) {
                    playClick();
                    gameBoard.undoMove();
                }
            }
        });
    }

    public void prepareGameOverDialog(){
        gameOverDialog.setContentView(R.layout.gameover_layout);
        Objects.requireNonNull(gameOverDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameOverDialog.setCancelable(false);

        Animation scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_anim);
        Button playAgainBtn = gameOverDialog.findViewById(R.id.btn_try_again);
        playAgainBtn.startAnimation(scaleAnim);
        playAgainBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                playClick();
                gameBoard.resetGame();
                gameOverDialog.dismiss();
                dialogOpen = false;
            }
        });
    }

    ////Dialogs
    public void showGameOverDialog() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView tvScore = gameOverDialog.findViewById(R.id.game_over_score_line);
                tvScore.setText(String.valueOf(score.getScore()));

                gameOverDialog.show();
            }
        });

    }
    public void ShowShufflingMsg(){
        //displaying shuffle msg
        final ImageView shufflingBackground = gameActivity.findViewById(R.id.shuffling_msg);
        final TextView shufflingText = gameActivity.findViewById(R.id.tv_shuffling);

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogOpen = true;
                shufflingText.setVisibility(VISIBLE);
                shufflingBackground.setVisibility(VISIBLE);
                shufflingText.setText(getResources().getString(R.string.shuffle));
                new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        shufflingText.setVisibility(GONE);
                        shufflingBackground.setVisibility(GONE);
                        dialogOpen = false;
                    }
                }.start();
            }
        });
    }
    public void firstTutorialScreen(){
        //displaying shuffle msg and placing it on top of score bar
        final ImageView tutorialBackground = gameActivity.findViewById(R.id.tutorial_background);
        final TextView tutorialText = gameActivity.findViewById(R.id.tv_tutorial);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialBackground.setVisibility(VISIBLE);
                tutorialText.setVisibility(VISIBLE);
                tutorialText.setText(gameActivity.getString(R.string.tutorial_first_line));
            }
        });
    }
    public void secondTutorialScreen(){
        //displaying shuffle msg and placing it on top of score bar
        final TextView tutorialText = gameActivity.findViewById(R.id.tv_tutorial);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText(gameActivity.getString(R.string.tutorial_second_line));

            }
        });
    }
    public void thirdTutorialScreen(){
        //displaying shuffle msg and placing it on top of score bar
        final ImageView tutorialBackground = gameActivity.findViewById(R.id.tutorial_background);
        final TextView tutorialText = gameActivity.findViewById(R.id.tv_tutorial);
        final Button endBtn = gameActivity.findViewById(R.id.btn_end_tutorial);
        Animation scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_anim);
        endBtn.startAnimation(scaleAnim);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText(gameActivity.getString(R.string.tutorial_third_line));
                endBtn.setVisibility(VISIBLE);
                dialogOpen = true;
                endBtn.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        if(isTutorialFromMainScreen){
                            ImageButton homeBtn = gameActivity.findViewById(R.id.btn_home);
                            homeBtn.callOnClick();

                        } else {
                            playClick();
                            endBtn.clearAnimation();
                            tutorialBackground.setVisibility(GONE);
                            tutorialText.setVisibility(GONE);
                            endBtn.setVisibility(GONE);
                            gameBoard.setTutorialFinished();
                            isTutorial = false;
                            dialogOpen = false;
                        }
                    }
                });



            }
        });
    }
    public void showAnnouncingMsg(final String msg){
        //displaying announcing msg
        final ImageView msgBackground = gameActivity.findViewById(R.id.shuffling_msg);
        final TextView msgText = gameActivity.findViewById(R.id.tv_shuffling);

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgText.setText(msg);
                dialogOpen = true;
                msgText.setVisibility(VISIBLE);
                msgBackground.setVisibility(VISIBLE);
                msgText.setTextSize(60);

                new CountDownTimer(2000, 100) {
                    int count = 0;
                    @Override
                    public void onTick(long millisUntilFinished) {
                        switch (count){
                            case 0:
                                msgText.setTextColor(getResources().getColor(R.color.value2));
                                count++;
                                break;
                            case 1:
                                msgText.setTextColor(getResources().getColor(R.color.value4));
                                count++;
                                break;
                            case 2:
                                msgText.setTextColor(getResources().getColor(R.color.value8));
                                count++;
                                break;
                            default:
                                msgText.setTextColor(getResources().getColor(R.color.value16));
                                count=0;
                                break;
                        }
                    }
                    @Override
                    public void onFinish() {
                        msgText.setVisibility(GONE);
                        msgBackground.setVisibility(GONE);
                        dialogOpen = false;
                    }
                }.start();
            }
        });
    }



    public void playClick() {
        final MediaPlayer click = MediaPlayer.create(gameActivity, R.raw.button_click);
        if (gameActivity.isSoundPlayed()) {
            click.start();
        }
    }
    public void playSwipe() {
        if (gameActivity.isSoundPlayed()) {
            swipe.start();
        }
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }
    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

}











