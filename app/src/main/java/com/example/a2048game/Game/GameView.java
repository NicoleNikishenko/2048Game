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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import com.example.a2048game.MainActivity;
import com.example.a2048game.R;
import com.example.a2048game.Tiles.BitmapCreator;



public class GameView  extends SurfaceView  implements SurfaceHolder.Callback{

    private static final String APP_NAME = "2048Project";

    private MainThread thread;
    private boolean isInit;
    private Score score;
    GameBoard gameBoard;
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

        isInit = false;
        int exponent = mainActivity.getBoardExponent();
        int rows = mainActivity.getBoardRows();
        int cols = mainActivity.getBoardCols();

        this.score = new Score(getResources(), (long)0, getContext().getSharedPreferences(APP_NAME,Context.MODE_PRIVATE));
        gameBoard = new GameBoard(rows, cols, exponent, this);
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
        prepareGameOverDialog();
        initSwipeListener();
        initClickListeners();
        gameBoard.initBoard();
        mainActivity.updateScore(score.getScore(),score.getTopScore());
        isInit=true;

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
        if(gameBoard.isGameOver()){
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
        gameBoard.draw(canvas);
    }



    private void drawEmptyBoard (Canvas canvas){
        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());

        //adding padding to frame


        int padding = (int) pxFromDp(3);

        if (gameBoard.getRows() == 6){
            padding = (int) pxFromDp(1);
        }

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


    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }


    private void initSwipeListener() {
        setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
            public void onSwipeTop() {
               gameBoard.up();
            }
            public void onSwipeRight() {
               gameBoard.right();

            }
            public void onSwipeLeft() {
                gameBoard.left();
            }
            public void onSwipeBottom() {
                gameBoard.down();
            }
        });
    }

    private void initClickListeners() {
        ImageButton resetBtn = mainActivity.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gameBoard.resetGame();
                mainActivity.updateScore(score.getScore(),score.getTopScore());
            }
        });

        ImageButton undoBtn = mainActivity.findViewById(R.id.btn_undo);
        undoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gameBoard.undoMove();
                mainActivity.updateScore(score.getScore(),score.getTopScore());
            }
        });

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    public void updateScore(long value){
        score.updateScore(value);
        mainActivity.updateScore(score.getScore(),score.getTopScore());

    }

    public void prepareGameOverDialog(){
        gameOverDialog.setContentView(R.layout.gameover_layout);
        gameOverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn = gameOverDialog.findViewById(R.id.btn_try_again);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                gameBoard.resetGame();
                gameOverDialog.dismiss();

            }
        });
    }

    public void showGameOverDialog() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameOverDialog.show();
            }
        });
    }



}














