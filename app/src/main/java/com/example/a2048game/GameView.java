package com.example.a2048game;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.a2048game.Tiles.BitmapCreator;

import java.util.zip.Inflater;


public class GameView  extends SurfaceView  implements SurfaceHolder.Callback{

    private static final String APP_NAME = "2048Project";

    private int scWidth, scHeight;
    private Context context;
    private MainThread thread;
    GameBoard gameBoard;
    Drawable backgroundRectangle = getResources().getDrawable(R.drawable.background_rectangle);
    Drawable cellRectangle = getResources().getDrawable(R.drawable.cell_rectangle);
    boolean isInit;

    SurfaceHolder holder = this.getHolder();
    AlertDialog.Builder builder;

    private Score score;
    MainActivity mainActivity;





    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        scWidth = dm.widthPixels;
        scHeight = dm.widthPixels;

        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        isInit = false;
        initSwipeListener();
        int exponent = 2;
        this.score = new Score(getResources(), 0, getContext().getSharedPreferences(APP_NAME,Context.MODE_PRIVATE));

        gameBoard = new GameBoard(4, 4, exponent, this);
        BitmapCreator.exponent= exponent;

        this.context = context;
        builder = new AlertDialog.Builder(MainActivity.getContext());

        this.mainActivity = (MainActivity)context;

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(holder, this);
        thread.setRunning(true);
        thread.start();
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

        if (!isInit) {
            gameBoard.initBoard();
        } isInit=true;

        gameBoard.draw(canvas);
    }


    public static void showGameOverDialog() {
        Looper.prepare();

        Toast.makeText(MainActivity.getContext(), "GameOverDialog", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext());

        LayoutInflater inflater = (LayoutInflater) MainActivity.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.gameover_layout, null, false);

        builder.setView(dialogView).setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

        Looper.loop();
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


    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }


    private void initSwipeListener() {
        setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
            public void onSwipeTop() { gameBoard.up(); }
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

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    public void updateScore(int value){


        score.updateScore(value);
        mainActivity.updateScore(score.getScore(),score.getTopScore());

    }

}














