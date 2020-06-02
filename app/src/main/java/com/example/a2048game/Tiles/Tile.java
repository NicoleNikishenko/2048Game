package com.example.a2048game.Tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.example.a2048game.GameBoard;



public class Tile {
    private GameBoard callback;

    private int value;
    private int currentPositionX;
    private int currentPositionY;
    private int desPositionX;
    private int desPositionY;
    private Position currentPosition;
    private Position desPosition;

    private boolean isMoving = false;
    private boolean increased = false;

    private BitmapCreator bitmapCreator = new BitmapCreator();

    private int currentCellHeight;
    private int currentCellWidth;
    private int defaultCellHeight;
    private int defaultCellWidth;

    //constructor
    public Tile(int value, Position position, GameBoard callback) {
        this.value = value;
        this.callback = callback;

        defaultCellHeight = currentCellHeight = bitmapCreator.getCellDefaultHeight();
        defaultCellWidth = currentCellWidth = bitmapCreator.getCellDefaultWidth();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellHeight = (defaultCellHeight/3);
        currentCellWidth = (defaultCellWidth/3);
    }

    //getters and setters
    public int getValue() { return value; }
    public Position getPosition() { return currentPosition; }

    public boolean notAlreadyIncreased(){ return !increased; }
    public void setIncreased(boolean state) { increased = state; }

    public void move(Position position){
        this.desPosition = position;
        desPositionX = desPosition.getPositionX();
        desPositionY = desPosition.getPositionY();
        this.isMoving = true;
    }

    public void increaseValue(){
        this.value *= callback.getExponent();
        currentCellHeight = (int)(defaultCellHeight*1.5);
        currentCellWidth = (int)(defaultCellWidth*1.5);
        increased = false;
    }


   public void draw (Canvas canvas){
        Bitmap bitmap = bitmapCreator.getBitmap(value);
        bitmap = Bitmap.createScaledBitmap(bitmap,currentCellWidth,currentCellHeight,false);
        canvas.drawBitmap(bitmap ,(int)(currentPositionX + (double)(defaultCellWidth/callback.getExponent()-currentCellWidth/callback.getExponent()))
                                 ,(int)(currentPositionY + (double)(defaultCellHeight/callback.getExponent()-currentCellHeight/callback.getExponent())),null);
        if(isMoving && currentPosition == desPosition ){
            isMoving =  false;
            if(increased)
               increaseValue();
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
        //resizing animation
       int sizeSpeed =10;

        if(currentCellHeight < defaultCellHeight || currentCellWidth < defaultCellWidth){
            if(currentCellHeight + sizeSpeed > defaultCellHeight || currentCellWidth + sizeSpeed > defaultCellWidth){
                currentCellHeight = defaultCellHeight;
                currentCellWidth = defaultCellWidth;
            } else {
                currentCellHeight += sizeSpeed;
                currentCellWidth += sizeSpeed;
            }
        }
       if(currentCellHeight > defaultCellHeight || currentCellWidth > defaultCellWidth){
           if(currentCellHeight - sizeSpeed < defaultCellHeight || currentCellWidth - sizeSpeed < defaultCellWidth){
               currentCellHeight = defaultCellHeight;
               currentCellWidth = defaultCellWidth;
           } else {
               currentCellHeight -= sizeSpeed;
               currentCellWidth -= sizeSpeed;
           }
       }

   }
   public void updatePosition(){
       //sliding animation
       int movingSpeed = 100;

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


}
