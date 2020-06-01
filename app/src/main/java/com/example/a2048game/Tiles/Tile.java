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
    private int movingSpeed = 80;
    private int sizeSpeed =10;

    private int currentPositionX;
    private int currentPositionY;
    private int desPositionX;
    private int desPositionY;

    private boolean isMoving = false;

    private boolean increased = false;


    private final Paint mPaint = new Paint();
    private TileDrawable drawables = new TileDrawable();
    private Rect textBounds = new Rect();
    private HashMap<Integer, Bitmap> tileBitmaps = new HashMap<>();

    private int currentCellSize;
    private int defaultCellSize;



    public Tile(int value, Position position, GameBoard callback) {
        this.value = value;
        this.callback = callback;

        this.defaultCellSize = drawables.getCellDefaultHeight();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellSize = (int)(defaultCellSize/3);

        if (tileBitmaps.isEmpty()){
            initCellBitmaps();
        }

    }

    public int getValue() { return value; }
    public Position getPosition() { return currentPosition; }
    public boolean isIncreased(){ return increased; }



    public void setIncreased(boolean state) { increased = state; }

    public void move(Position position){

        this.desPosition = position;
        desPositionX = desPosition.getPositionX();
        desPositionY = desPosition.getPositionY();

        this.isMoving = true;

    }


   public void draw (Canvas canvas){
        Bitmap bitmap = getBitmap(value);
        bitmap = Bitmap.createScaledBitmap(bitmap,currentCellSize,currentCellSize,false);
        canvas.drawBitmap(bitmap,currentPositionX+ (int)(defaultCellSize/2-currentCellSize/2) ,currentPositionY + (int)(defaultCellSize/2-currentCellSize/2),null);
        if(isMoving && currentPosition == desPosition ){
            isMoving =  false;
            if(increased){
                this.value *=2;
                currentCellSize = (int)(defaultCellSize*1.5);
                increased = false;
            }
            callback.finishedMoving(this);

        }
   }

   public void update() {
        if(isMoving) {
            updatePosition();
        }
       updateSize();

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

       if (currentPositionX < desPositionX) {
           if (currentPositionX + movingSpeed > desPositionX) {
              currentPosition = desPosition;
              currentPositionX = currentPosition.getPositionX();
           } else {
               currentPositionX += movingSpeed ;
           }
       } else if (currentPositionX > desPositionX) {
           if (currentPositionX - movingSpeed  < desPositionX) {
               currentPosition = desPosition;
               currentPositionX = currentPosition.getPositionX();
           } else {
               currentPositionX -= movingSpeed ;
           }
       }
       if (currentPositionY < desPositionY) {
           if (currentPositionY + movingSpeed  > desPositionY) {
               currentPosition = desPosition;
               currentPositionY = currentPosition.getPositionY();
           } else {
               currentPositionY += movingSpeed ;
           }
       } else if (currentPositionY > desPositionY) {
           if (currentPositionY - movingSpeed  < desPositionY) {
               currentPosition = desPosition;
               currentPositionY = currentPosition.getPositionY();
           } else {
               currentPositionY -= movingSpeed ;
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
