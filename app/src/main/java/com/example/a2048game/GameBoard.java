package com.example.a2048game;

import java.util.Random;

public class GameBoard {
    private Tile[][] board;
    int width;
    int height;
    Random rand = new Random();


    public GameBoard(int x, int y) {
        width=x;
        height=y;
        board = new Tile[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public Tile getTile(int x, int y){
        return board[x][y];
    }
    public void setTile(int x, int y, int value){
        Tile tile = getTile(x,y);
        if(tile == null)
        {
            board[x][y] = new Tile(value,x,y);
        }else{
            tile.value = value;
        }
    }

    public boolean mergeTile(int fromx, int fromy, int tox, int toy) {
        Tile fromtile = getTile(fromx,fromy);
        Tile totile = getTile(tox,toy);
        if (fromtile == null)
        {
            return false;
        }
        if (totile!=null) {
            if (totile.value==fromtile.value)
            {
                board[fromx][fromy] = null;
                totile.value*=2;
                return true;
            }
        }
        return false;
    }

    public boolean moveTile(int fromx, int fromy, int tox, int toy) {
        Tile fromtile = getTile(fromx,fromy);
        Tile totile = getTile(tox,toy);
        if (fromtile == null)
        {
            return false;
        }
        if (totile==null) {
            board[fromx][fromy] = null;
            board[tox][toy]=fromtile;
            return true;
        }
        return false;
    }

    void addRandom() {
        int count=0;
        for (int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if (getTile(x,y)==null)
                    count++;
            }
        }
        int number = rand.nextInt(count);
        count =0;
        for (int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if (getTile(x,y)==null) {
                    if(count == number){
                        setTile(x,y,2);
                        return;
                    }
                    count++;
                }
            }
        }



    }

    void up(){
        for(int x=0;x<width;x++){
            for (int y=1;y<height;y++){
                boolean moved = moveTile(x,y,x,y-1);
                if (moved) {
                    y = 0;
                }
            }
            for (int y=1;y<height;y++){
                boolean merged = mergeTile(x,y,x,y-1);
            }
            for (int y=1;y<height;y++){
                boolean moved = moveTile(x,y,x,y-1);
                if (moved) {
                    y = 0;
                }
            }
        }
    }
    void down(){
        for(int x=0;x<width;x++){
            for (int y=height-1;y>0;y--){
                boolean moved = moveTile(x,y-1,x,y);
                if (moved) {
                    y = height;
                }
            }
            for (int y=height-1;y>0;y--){
                boolean merged = mergeTile(x,y-1,x,y);
            }
            for (int y=height-1;y>0;y--){
                boolean moved = moveTile(x,y-1,x,y);
                if (moved) {
                    y = height;
                }
            }
        }
    }
    void left() {
        for(int y=0;y<height;y++){
            for (int x=1;x<width;x++){
                boolean moved = moveTile(x,y,x-1,y);
                if (moved) {
                    x = 0;
                }
            }
            for (int x=1;x<width;x++){
                boolean merged = mergeTile(x,y,x-1,y);
            }
            for (int x=1;x<width;x++){
                boolean moved = moveTile(x,y,x-1,y);
                if (moved) {
                    x = 0;
                }
            }
        }
    }
    void right(){
        for(int y=0;y<height;y++){
            for (int x=width-1;x>0;x--){
                boolean moved = moveTile(x-1,y,x,y);
                if (moved) {
                    x = width;
                }
            }
            for (int x=width-1;x>0;x--){
                boolean merged = mergeTile(x-1,y,x,y);
            }
            for (int x=width-1;x>0;x--){
                boolean moved = moveTile(x-1,y,x,y);
                if (moved) {
                    x = width;
                }
            }
        }
    }

}
