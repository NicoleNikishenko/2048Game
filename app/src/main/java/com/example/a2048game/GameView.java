package com.example.a2048game;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.a2048game.Tiles.TileDrawable;


public class GameView  extends SurfaceView  implements SurfaceHolder.Callback{

    private MainThread thread;
    GameBoard gameBoard;
    Drawable backgroundRectangle = getResources().getDrawable(R.drawable.background_rectangle);
    Drawable cellRectangle = getResources().getDrawable(R.drawable.cell_rectangle);
    TileDrawable tileDrawable;
    boolean isInit;



    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);


        tileDrawable = new TileDrawable();
        tileDrawable.setContext(context);

        isInit = false;
        initSwipeListener();
        gameBoard = new GameBoard(4, 4);
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

    private void drawEmptyBoard (Canvas canvas){
        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());

        //adding padding to frame

        int padding = (int) pxFromDp(3);
        int width = getWidth() - padding * 2;
        int height = getHeight() - padding * 2;

        //calculating cell size
        int cellWidth = width / gameBoard.getWidth();
        int cellHeight = height / gameBoard.getHeight();

        tileDrawable.setCellDefaultHeight(cellHeight);
        tileDrawable.setCellDefaultWidth(cellWidth);

        //drawing empty tiles
        for (int x = 0; x < gameBoard.getHeight(); x++) {
            for (int y = 0; y < gameBoard.getWidth(); y++) {

                //start cell position
                int posX = x * cellHeight + padding;
                int posY = y * cellWidth + padding;

                //end cell position
                int posXX = posX + cellHeight;
                int posYY = posY + cellWidth;


                cellRectangle.setColorFilter(getResources().getColor(R.color.valueEmpty), PorterDuff.Mode.SRC_OVER);
                drawDrawable(canvas, cellRectangle, posX, posY, posXX, posYY);

                //filling positions Matrix only on init
                if (!isInit) {
                    gameBoard.setPositions(x, y, posY, posX);
                }
            }
        }
    }

    private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

}














