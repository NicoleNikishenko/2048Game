package com.example.a2048game.Tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.example.a2048game.Game.GameBoard;



public class Tile {
    private GameBoard callback;

    private long value;
    private int currentPositionX;
    private int currentPositionY;
    private int desPositionX;
    private int desPositionY;
    private Position currentPosition;
    private Position desPosition;

    private boolean isMoving = false;
    private boolean increased = false;
    private boolean isSolid = false;
    private boolean isSolidGone=false;
    private int solidLives;
    private BitmapCreator bitmapCreator = new BitmapCreator();

    private int currentCellHeight;
    private int currentCellWidth;
    private int defaultCellHeight;
    private int defaultCellWidth;

    //constructor
    public Tile(long value, Position position, GameBoard callback) {
        this.value = value;
        this.callback = callback;

        defaultCellHeight = currentCellHeight = bitmapCreator.getCellDefaultHeight();
        defaultCellWidth = currentCellWidth = bitmapCreator.getCellDefaultWidth();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellHeight = (defaultCellHeight/5);
        currentCellWidth = (defaultCellWidth/5);
    }

    public Tile(int value, Position position, GameBoard callback, int solidLives) {
        this.value = value;
        this.callback = callback;

        defaultCellHeight = currentCellHeight = bitmapCreator.getCellDefaultHeight();
        defaultCellWidth = currentCellWidth = bitmapCreator.getCellDefaultWidth();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellHeight = (defaultCellHeight/5);
        currentCellWidth = (defaultCellWidth/5);

        this.isSolid = true;
        this.solidLives = solidLives;
    }

    //getters and setters
    public long getValue() { return value; }
    public Position getPosition() { return currentPosition; }
    public boolean isSolid(){ return isSolid;}
    public void  decreaseLiveCount(){ this.solidLives--; }



    public boolean notAlreadyIncreased(){ return !increased; }
    public void setIncreased(boolean state) { increased = state; }
    public Tile copyTile (){
        return new Tile(this.value, this.currentPosition, this.callback);
    }

    public void move(Position position){
        this.desPosition = position;
        desPositionX = desPosition.getPositionX();
        desPositionY = desPosition.getPositionY();
        this.isMoving = true;
    }

    public void increaseValue(){
        this.value *= callback.getExponent();
        currentCellHeight = (int)(defaultCellHeight*1.4);
        currentCellWidth = (int)(defaultCellWidth*1.4);
        increased = false;
    }


   public void draw (Canvas canvas){
        Bitmap bitmap = bitmapCreator.getBitmap(value);
        bitmap = Bitmap.createScaledBitmap(bitmap,currentCellWidth,currentCellHeight,false);
        canvas.drawBitmap(bitmap ,(int)(currentPositionX + (double)(defaultCellWidth/callback.getExponent()-currentCellWidth/callback.getExponent()))
                                 ,(int)(currentPositionY + (double)(defaultCellHeight/callback.getExponent()-currentCellHeight/callback.getExponent())),null);
        if(isMoving && currentPosition == desPosition && currentCellWidth == defaultCellWidth){
            isMoving =  false;
            if(increased) {
                callback.updateScore(this.getValue());
                increaseValue();
            }
            callback.finishedMoving(this);
        }
   }

   public void update() {
        if(isMoving) {
            updatePosition();
        }
       if(isSolid && solidLives == 1){
           removeSolidBlock();
           return;
       }
      updateSize();

   }

   public void updateSize(){
        //resizing animation
       int sizeSpeed =20;

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

    public boolean needsToUpdate(){
        return currentPosition != desPosition || currentCellWidth != defaultCellWidth;
    }

    void removeSolidBlock(){
        int sizeSpeed =20;
        if(currentCellHeight - sizeSpeed <= 0 || currentCellWidth - sizeSpeed <= 0){
            isSolidGone = true;
        }
        currentCellHeight = currentCellHeight - sizeSpeed;
        currentCellWidth = currentCellHeight - sizeSpeed;
    }
    public Boolean isSolidGone(){
        return isSolidGone;
    }
}
