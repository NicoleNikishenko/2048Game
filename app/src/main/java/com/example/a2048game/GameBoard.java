package com.example.a2048game;

import android.graphics.Canvas;

import com.example.a2048game.Tiles.Position;
import com.example.a2048game.Tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard {


    private Tile[][] board;
    private Tile[][] newBoard;
    private Position[][] positions;
    private int boardWidth;
    private int boardHeight;
    Random rand = new Random();

    private boolean isMoving = false;
    private ArrayList<Tile> movingTiles;

    //constructor
    public GameBoard(int x, int y) {
        boardWidth=x;
        boardHeight=y;
        board = new Tile[x][y];
        positions = new Position[x][y];
    }

    //get and set
    public int getWidth() { return boardWidth; }
    public int getHeight() { return boardHeight; }
    public Tile getTile(int x, int y) { return board[x][y]; }
    public Position getPositions (int matrixX, int matrixY){
        return positions[matrixX][matrixY];
    }


    public void setPositions (int matrixX ,int matrixY ,int positionX,int positionY){
        Position position = new Position(positionX,positionY);
        positions[matrixX][matrixY]= position;
    }

    public void initBoard(){
        addRandom();
        addRandom();
        movingTiles = new ArrayList<>();
    }
    void addRandom() {
        int count = 0;
        for (int y = 0; y < boardHeight; y++){
            for(int x = 0; x < boardWidth; x++){
                if (getTile(x, y) == null)
                    count++;
            }
        }
        int number = rand.nextInt(count);
        count = 0;
        for (int y = 0; y < boardHeight; y++){
            for(int x = 0; x < boardWidth; x++){
                if (getTile(x, y)==null) {
                    if(count == number){
                        board[x][y] = new Tile(2,getPositions(x,y),this);
                        return;
                    }
                    count++;
                }
            }
        }

    }

    public void draw(Canvas canvas){
        for (int x = 0; x < boardWidth; x++) {

            for (int y = 0; y < boardHeight; y++) {

                if (board[x][y] != null) {

                    board[x][y].draw(canvas);
                }
            }
        }
    }

    public void update() {
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (board[x][y] != null) {
                    board[x][y].update();
                }
            }
        }
    }



    void up(){
        isMoving = true;
        for(int x=0;x<boardWidth;x++){
            for (int y=1;y<boardHeight;y++){
                mergeTileFirst(x,y,x,y-1);
            }
        }
        for(int x=0;x<boardWidth;x++){
            for (int y=1;y<boardHeight;y++){
                boolean moved = moveTile(x,y,x,y-1);
                if (moved) {
                    y = 0;
                }
            }
        }
        for(int x=0;x<boardWidth;x++){
            for (int y=1;y<boardHeight;y++){
                mergeTileSecond(x,y,x,y-1);
            }
        }
        checkMatrixChanges();
    }
    void down(){
        isMoving = true;
        for(int x=0;x<boardWidth;x++){
            for (int y=boardHeight-1;y>0;y--){
                mergeTileFirst(x,y-1,x,y);
            }
        }
       for(int x=0;x<boardWidth;x++){
            for (int y=boardHeight-1;y>0;y--){
                boolean moved = moveTile(x,y-1,x,y);
                if (moved) {
                    y = boardHeight;
                }
            }
        }
       for(int x=0;x<boardWidth;x++){
            for (int y=boardHeight-1;y>0;y--){
                mergeTileSecond(x,y-1,x,y);
            }
        }
        checkMatrixChanges();

    }
    void left() {

        isMoving = true;

        for(int y=0;y<boardHeight;y++){
            for (int x=1;x<boardWidth;x++){
                mergeTileFirst(x,y,x-1,y);
            }
        }
        for(int y=0;y<boardHeight;y++){
            for (int x=1;x<boardWidth;x++){
                boolean moved = moveTile(x,y,x-1,y);
                if (moved) {
                    x = 0;
                }
            }
        }
        for(int y=0;y<boardHeight;y++){
            for (int x=1;x<boardWidth;x++){
                mergeTileSecond(x,y,x-1,y);
            }
        }
        checkMatrixChanges();
    }

    void right(){

        isMoving = true;
        for(int y=0;y<boardHeight;y++){
            for (int x=boardWidth-1;x>0;x--){
                mergeTileFirst(x-1,y,x,y);
            }
        }
        for(int y=0;y<boardHeight;y++){
            for (int x=boardWidth-1;x>0;x--){
                boolean moved = moveTile(x-1,y,x,y);
                if (moved) {
                    x = boardWidth;
                }
            }
        }
        for(int y=0;y<boardHeight;y++){
            for (int x=boardWidth-1;x>0;x--){
                mergeTileSecond(x-1,y,x,y);
            }
        }
        checkMatrixChanges();


    }

    public void mergeTileFirst(int fromX, int fromY, int toX, int toY) {
        Tile fromTile = getTile(fromX,fromY);
        Tile toTile = getTile(toX,toY);
        if (fromTile == null)
        {
            return;
        }
        if (toTile!=null) {
            if (toTile.getValue( )==fromTile.getValue())
            {
                board[fromX][fromY] = null;
                int value = toTile.getValue();
                toTile.increaseValue(value*2);
                toTile.setMerged(true);
            }
        }

    }
    public void mergeTileSecond(int fromX, int fromY, int toX, int toY) {
        Tile fromTile = getTile(fromX, fromY);
        Tile toTile = getTile(toX, toY);
        if (fromTile == null) {
            return;
        }
        if (toTile != null && !toTile.isAlreadyMerged() && !fromTile.isAlreadyMerged()) {
            if (toTile.getValue() == fromTile.getValue()) {
                fromTile.setDead(true);
                toTile.setIncreased(true);

            }
        }
    }

    public boolean moveTile(int fromX, int fromY, int toX, int toY) {
        Tile fromTile = getTile(fromX,fromY);
        Tile toTile = getTile(toX,toY);
        if (fromTile == null)
        {
            return false;
        }
        if (toTile==null) {
            board[fromX][fromY] = null;
            board[toX][toY]=fromTile;
            return true;
        }
        return false;
    }

    public void checkMatrixChanges() {
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Tile t = board[x][y];
                if(t != null) {
                    if (t.getPosition() != getPositions(x, y)) {
                        movingTiles.add(t);
                        t.move(getPositions(x, y));
                    }
                }
            }
        }
    }


    public void finishedMoving(Tile t) {
        movingTiles.remove(t);
        if (movingTiles.isEmpty()) {
            checkTilesState();
            isMoving = false;

        }
    }

    public void checkTilesState(){
        for(int x=0;x<boardWidth;x++){
            for (int y=0;y<boardHeight;y++){
                if (board[x][y]!=null){
                    Tile tile = board[x][y];
                    if (tile.isDead()){
                        board[x][y]=null;
                    }
                    else if (tile.isIncreased()){
                        int value = tile.getValue();
                        tile.increaseValue(value*2);
                        tile.setIncreased(false);
                    }
                    else if(tile.isAlreadyMerged()){
                        tile.setMerged(false);
                    }
                }
            }
        }
        addRandom();
    }

}
