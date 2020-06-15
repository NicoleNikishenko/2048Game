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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.a2048game.MainActivity;
import com.example.a2048game.R;
import com.example.a2048game.Tiles.BitmapCreator;

import static android.content.Context.MODE_PRIVATE;


public class GameView  extends SurfaceView  implements SurfaceHolder.Callback{

    private static final String APP_NAME = "2048Project";
    private static final String SELECTED_GAME_MODE = "gameMode";
    private static final int GAME_MODE_CLASSIC = 0;
    private static final int GAME_MODE_SOLID_TILE = 1;
    private static final int GAME_MODE_SHUFFLE = 2;

    private MediaPlayer swipe;
    private MainThread thread;
    private boolean isInit;
    private boolean isTutorial;
    private Score score;
    GameBoard gameBoard;
    Boolean dialogOpen = false;
    Drawable backgroundRectangle = getResources().getDrawable(R.drawable.gameboard_background);
    Drawable cellRectangle = getResources().getDrawable(R.drawable.gameboard_cell_shape);
    AlertDialog.Builder builder;
    MainActivity mainActivity;
    private Dialog gameOverDialog;





    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);


        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.mainActivity = (MainActivity)context;
        swipe = MediaPlayer.create(mainActivity , R.raw.swipe);

        isInit = false;
        int exponent = mainActivity.getBoardExponent();
        int rows = mainActivity.getBoardRows();
        int cols = mainActivity.getBoardCols();
        int gameMode = mainActivity.getGameMode();
        isTutorial = mainActivity.isTutorial();


        this.score = new Score(getResources(), (long)0, getContext().getSharedPreferences(APP_NAME,Context.MODE_PRIVATE),gameMode, rows, cols);


        //gameMode = getContext().getSharedPreferences(APP_NAME,Context.MODE_PRIVATE).getInt(SELECTED_GAME_MODE,1);
        gameBoard = new GameBoard(rows, cols, exponent, this ,gameMode);
        BitmapCreator.exponent = exponent;

        builder = new AlertDialog.Builder(MainActivity.getContext());
        gameOverDialog = new Dialog(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(holder, this);
        mainActivity.setThread(thread);
        thread.setRunning(true);
        thread.start();


        //Initializing board
        mainActivity.updateScore(score.getScore(),score.getTopScore());
        initClickListeners();
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
            if(score.isNewHighScore()) //if we have a new highscore we will update the entire leaderboard. else we will check if we got a new midscore and if so we will update the leaderboard appropriately
                score.updateLeaderBoard();
            else
                score.checkIfNewMidScore();
            showGameOverDialog();
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
        mainActivity.updateScore(score.getScore(),score.getTopScore());

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
    private void initClickListeners() {
        ImageButton resetBtn = mainActivity.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isTutorial) {
                    playClick();
                    if (score.isNewHighScore())
                        score.updateLeaderBoard();
                    else
                        score.checkIfNewMidScore();
                    score.refreshLeaderBoard();
                    gameBoard.resetGame();
                }

            }
        });

        ImageButton undoBtn = mainActivity.findViewById(R.id.btn_undo);
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
        gameOverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameOverDialog.setCancelable(false);
        Button btn = gameOverDialog.findViewById(R.id.btn_try_again);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                playClick();
                gameBoard.resetGame();
                gameOverDialog.dismiss();
                dialogOpen = false;
            }
        });
    }
    public void showGameOverDialog() {
        mainActivity.runOnUiThread(new Runnable() {
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
        final ImageView shufflingBackground = mainActivity.findViewById(R.id.shuffling_msg);
        final TextView shufflingText = mainActivity.findViewById(R.id.tv_shuffling);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogOpen = true;
                shufflingText.setVisibility(VISIBLE);
                shufflingBackground.setVisibility(VISIBLE);
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
        final ImageView tutorialBackground = mainActivity.findViewById(R.id.tutorial_background);
        final TextView tutorialText = mainActivity.findViewById(R.id.tv_tutorial);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialBackground.setVisibility(VISIBLE);
                tutorialText.setVisibility(VISIBLE);
                tutorialText.setText("Swipe Up/Down/left/right to move the tiles across the board");
            }
        });
    }
    public void secondTutorialScreen(){
        //displaying shuffle msg and placing it on top of score bar
        final TextView tutorialText = mainActivity.findViewById(R.id.tv_tutorial);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText("GOOD!! \nTwo identical tiles can merge, swipe to merge your tiles ");

            }
        });
    }
    public void thirdTutorialScreen(){
        //displaying shuffle msg and placing it on top of score bar
        final ImageView tutorialBackground = mainActivity.findViewById(R.id.tutorial_background);
        final TextView tutorialText = mainActivity.findViewById(R.id.tv_tutorial);
        final Button end = mainActivity.findViewById(R.id.btn_end_tutorial);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText("Perfect!! \n Marge more and more tiles and try to get a higher score");
                end.setVisibility(VISIBLE);
                dialogOpen = true;
                end.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        playClick();
                        tutorialBackground.setVisibility(GONE);
                        tutorialText.setVisibility(GONE);
                        end.setVisibility(GONE);
                        gameBoard.setTutorialFinished();
                        dialogOpen = false;
                    }
                });



            }
        });
    }


    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }



    public void playClick() {
        final MediaPlayer click = MediaPlayer.create(mainActivity , R.raw.button_click);
        if (!mainActivity.isSoundMuted()) {
            click.start();
        }
    }
    public void playSwipe() {
        if (!mainActivity.isSoundMuted()) {
            swipe.start();
        }
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

}











