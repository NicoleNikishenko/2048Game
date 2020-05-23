package com.example.a2048game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GameView extends View {

    private final Paint mPaint = new Paint();
    Bitmap mBackground;
    private Drawable mBackgroundRectangle;
    private Drawable[] mCellRectangle = new Drawable[20];
    GameBoard gameBoard = new GameBoard(4,4);
    Rect textBounds = new Rect();


    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDrawable(canvas, mBackgroundRectangle, 0, 0, canvas.getWidth(), canvas.getHeight());

        int padding = (int)pxFromDp(3);
        int width=canvas.getWidth()-padding*2;
        int height=canvas.getHeight()-padding*2;
        int cellWidth=width / gameBoard.getWidth();
        int cellHeight=height / gameBoard.getHeight();


        // drawDrawable(canvas, mBackgroundRectangle, 0, 0, cellWidth*gameBoard.getWidth(), cellHeight* gameBoard.getHeight());
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(true);



        for (int y=0;y<gameBoard.getHeight();y++){
            for (int x=0; x<gameBoard.getWidth();x++){
                int posX = x*cellWidth+padding;
                int posY = y*cellHeight+padding;
                Tile tile = gameBoard.getTile(x,y);

                if (tile == null){
                    drawDrawable(canvas, mCellRectangle[0],posX,posY,posX+cellWidth,posY+cellHeight);
                } else {
                    String text= Integer.toString(tile.getValue());
                    drawDrawable(canvas, mCellRectangle[1], posX, posY, posX + cellWidth, posY + cellHeight);
                    mPaint.getTextBounds(text, 0, text.length(), textBounds);
                    canvas.drawText(text, posX + cellWidth / 2 - textBounds.exactCenterX(), posY + cellHeight / 2 - textBounds.exactCenterY(), mPaint);
                }

            }
        }

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }



    private void init() {
        try {
            gameBoard.addRandom();
            gameBoard.addRandom();

            this.setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
                public void onSwipeTop() {
                    gameBoard.up();
                    gameBoard.addRandom();
                    invalidate();
                }
                public void onSwipeRight() {
                    gameBoard.right();
                    gameBoard.addRandom();
                    invalidate();

                }
                public void onSwipeLeft() {
                    gameBoard.left();
                    gameBoard.addRandom();
                    invalidate();

                }
                public void onSwipeBottom() {
                    gameBoard.down();
                    gameBoard.addRandom();
                    invalidate();
                }




            });


            mBackgroundRectangle = getResources().getDrawable(R.drawable.background_rectangle);
            mCellRectangle[0] = getResources().getDrawable(R.drawable.cell_rectangle);
            mCellRectangle[1] = getResources().getDrawable(R.drawable.cell_rectangle_2);

        } catch (Exception e) {

        }
    }



    private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }



}
