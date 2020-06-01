package com.example.a2048game.Tiles;

public class Position {
    private int positionX;
    private int positionY;

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public Position(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
    public void setPositions(int positionsX, int positionY){
        this.positionX = positionsX;
        this.positionY = positionY;
    }

}
