package com.example.a2048game.Tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.example.a2048game.GameBoard;

import java.util.HashMap;

public class Tile {
    private GameBoard callback;
    private int value;
    private Position currentPosition;
    private Position desPosition;
    private int movingSpeed = 60;
    private int sizeSpeed = 20;

    private boolean isMoving = false;
    private boolean dead = false;
    private boolean increased = false;
    private boolean alreadyMerged = false;

    private final Paint mPaint = new Paint();
    private TileDrawable drawables = new TileDrawable();
    private Rect textBounds = new Rect();
    private HashMap<Integer, Bitmap> tileBitmaps = new HashMap<>();


    ///////////////////////////////////////////
    private int currentCellSize;
    private int defaultCellSize;
    //////////////////////////////////////////

    public Tile(int value, Position position, GameBoard callback) {
        this.value = value;
        this.callback = callback;

        this.defaultCellSize = drawables.getCellDefaultHeight();

        currentPosition = desPosition = position;
        currentCellSize = defaultCellSize/2;



        if (tileBitmaps.isEmpty()){
            initCellBitmaps();
        }

    }

    public int getValue() { return value; }
    public Position getPosition() { return currentPosition; }

    public boolean isDead(){ return dead; }
    public boolean isIncreased(){ return increased; }
    public boolean isAlreadyMerged(){ return alreadyMerged;}

    public void setMerged(boolean state) { alreadyMerged = state; }
    public void setDead(boolean state) { dead = state; }
    public void setIncreased(boolean state) { increased = state; }

    public void move(Position position){
        this.isMoving = true;
        this.desPosition = position;
    }

    public void increaseValue(int value) {
        this.value = value;
        currentCellSize = (int)(defaultCellSize*1.5);
    }

   public void draw (Canvas canvas){
        Bitmap bitmap = getBitmap(value);
        bitmap = Bitmap.createScaledBitmap(bitmap,currentCellSize,currentCellSize,false);
        canvas.drawBitmap(bitmap,currentPosition.positionX + (int)(defaultCellSize/2-currentCellSize/2) ,currentPosition.positionY + (int)(defaultCellSize/2-currentCellSize/2),null);
        if(isMoving && currentPosition.positionX == desPosition.positionX && currentPosition.positionY == desPosition.positionY){
            isMoving =  false;
            callback.finishedMoving(this);
        }
   }

   public void update() {
        updateSize();
        updatePosition();
   }

   public void updateSize(){
        if(currentCellSize < defaultCellSize){
            if(currentCellSize + sizeSpeed > defaultCellSize){
                currentCellSize = defaultCellSize;
            } else {
                currentCellSize += sizeSpeed;
            }
        }
       if(currentCellSize > defaultCellSize){
           if(currentCellSize - sizeSpeed < defaultCellSize){
               currentCellSize = defaultCellSize;
           } else {
               currentCellSize -= sizeSpeed;
           }
       }

   }
   public void updatePosition(){
       if (currentPosition.positionX < desPosition.positionX) {
           if (currentPosition.positionX + movingSpeed > desPosition.positionX) {
               currentPosition.positionX = desPosition.positionX;
           } else {
               currentPosition.positionX += movingSpeed ;
           }
       } else if (currentPosition.positionX > desPosition.positionX) {
           if (currentPosition.positionX - movingSpeed  < desPosition.positionX) {
               currentPosition.positionX = desPosition.positionX;
           } else {
               currentPosition.positionX -= movingSpeed ;
           }
       }
       if (currentPosition.positionY < desPosition.positionY) {
           if (currentPosition.positionY + movingSpeed  > desPosition.positionY) {
               currentPosition.positionY = desPosition.positionY;
           } else {
               currentPosition.positionY += movingSpeed ;
           }
       } else if (currentPosition.positionY > desPosition.positionY) {
           if (currentPosition.positionY - movingSpeed  < desPosition.positionY) {
               currentPosition.positionY = desPosition.positionY;
           } else {
               currentPosition.positionY -= movingSpeed ;
           }
       }
   }



    public void initCellBitmaps() {

        Drawable drawable;


        //set text style
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(true);
        String text;
        int value;
        Bitmap bitmap;


        for(int i = 1 ; i <= 12 ; i++) {

            value = (int)Math.pow(2,i);
            drawable = drawables.createDrawable(value);
            text = Integer.toString(value);

            bitmap=Bitmap.createBitmap(defaultCellSize,defaultCellSize,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,defaultCellSize,defaultCellSize);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
            drawable.draw(canvas);
            canvas.drawText(text, (float)defaultCellSize/2 - textBounds.exactCenterX(), (float)defaultCellSize/2 - textBounds.exactCenterY(), mPaint);
            tileBitmaps.put(value,bitmap);
        }
    }



    public Bitmap getBitmap (int value){
        return tileBitmaps.get(value);
    }

}
