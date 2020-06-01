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
    private int boardRows;
    private int boardCols;

    Random rand = new Random();

    private boolean isMoving = false;
    private boolean spawnNeeded = false;
    private ArrayList<Tile> movingTiles;

    //constructor
    public GameBoard(int cols, int rows) {
        boardRows = rows;
        boardCols = cols;
        board = new Tile[rows][cols];
        positions = new Position[rows][cols];
    }

    //get and set
    public int getHeight() { return boardRows; }
    public int getWidth() { return boardCols; }
    public Tile getTile(int x, int y) { return board[x][y]; }
    public void setPositions (int matrixX ,int matrixY ,int positionX,int positionY){
        Position position = new Position(positionX,positionY);
        positions[matrixX][matrixY]= position;
    }

    public void initBoard(){
        addRandom();
        addRandom();
        addRandom();
        addRandom();
        movingTiles = new ArrayList<>();
    }

    void addRandom() {
    // a new tile is spawning in a random empty place on the board
        int count = 0;
        for (int x = 0; x < boardRows; x++){
            for(int y = 0; y < boardCols; y++){
                if (getTile(x, y) == null)
                    count++;
            }
        }
        int number = rand.nextInt(count);
        count = 0;
        for (int x = 0; x < boardRows; x++){
            for(int y = 0; y < boardCols; y++){
                if (getTile(x, y)==null) {
                    if(count == number){
                        board[x][y] = new Tile(2,positions[x][y],this);
                        return;
                    }
                    count++;
                }
            }
        }

    }

    public void draw(Canvas canvas){
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null) {
                    board[x][y].draw(canvas);
                }
            }
        }
    }

    public void update() {
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null) {
                    board[x][y].update();
                }
            }
        }
    }



    void up(){
        if (!isMoving) {
           isMoving = true;
            newBoard = new Tile[boardRows][boardCols];

            for (int i = 0; i < boardRows; i++) {
                for (int j = 0; j < boardCols; j++) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = board[i][j];
                        for (int k = i - 1; k >= 0; k--) {
                            if (newBoard[k][j] == null) {
                                newBoard[k][j] = board[i][j];
                                if (newBoard[k + 1][j] == board[i][j]) {
                                    newBoard[k + 1][j] = null;
                                }
                            } else if (newBoard[k][j].getValue() == board[i][j].getValue() && !newBoard[k][j].isIncreased()) {
                                newBoard[k][j] = board[i][j];
                                newBoard[k][j].setIncreased(true);
                                if (newBoard[k + 1][j] == board[i][j]) {
                                    newBoard[k + 1][j] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = newBoard;
        }
    }



    void down(){
        if (!isMoving) {
            isMoving = true;
            newBoard = new Tile[boardRows][boardCols];

            for (int i = 3; i >= 0; i--) {
                for (int j = 0; j < 4; j++) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = board[i][j];
                        for (int k = i + 1; k < 4; k++) {
                            if (newBoard[k][j] == null) {
                                newBoard[k][j] = board[i][j];
                                if (newBoard[k - 1][j] == board[i][j]) {
                                    newBoard[k - 1][j] = null;
                                }
                            } else if (newBoard[k][j].getValue() == board[i][j].getValue() && !newBoard[k][j].isIncreased()) {
                                newBoard[k][j] = board[i][j];
                                newBoard[k][j].setIncreased(true);
                                if (newBoard[k - 1][j] == board[i][j]) {
                                    newBoard[k - 1][j] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = newBoard;
        }
    }
    void left() {
        if (!isMoving) {
            isMoving = true;
            newBoard = new Tile[boardRows][boardCols];

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = board[i][j];
                        for (int k = j - 1; k >= 0; k--) {
                            if (newBoard[i][k] == null) {
                                newBoard[i][k] = board[i][j];
                                if (newBoard[i][k + 1] == board[i][j]) {
                                    newBoard[i][k + 1] = null;
                                }
                            } else if (newBoard[i][k].getValue() == board[i][j].getValue() && !newBoard[i][k].isIncreased()) {
                                newBoard[i][k] = board[i][j];
                                newBoard[i][k].setIncreased(true);
                                if (newBoard[i][k + 1] == board[i][j]) {
                                    newBoard[i][k + 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = newBoard;
        }
    }

    void right(){
        if (!isMoving) {
            isMoving = true;
            newBoard = new Tile[boardRows][boardCols];

            for (int i = 0; i < 4; i++) {
                for (int j = 3; j >= 0; j--) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = board[i][j];
                        for (int k = j + 1; k < 4; k++) {
                            if (newBoard[i][k] == null) {
                                newBoard[i][k] = board[i][j];
                                if (newBoard[i][k - 1] == board[i][j]) {
                                    newBoard[i][k - 1] = null;
                                }
                            } else if (newBoard[i][k].getValue() == board[i][j].getValue() && !newBoard[i][k].isIncreased()) {
                                newBoard[i][k] = board[i][j];
                                newBoard[i][k].setIncreased(true);
                                if (newBoard[i][k - 1] == board[i][j]) {
                                    newBoard[i][k - 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = newBoard;
        }
    }

    public void moveTiles(){
        //checking which tiles changed position and moving them accordingly
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                Tile t = newBoard[x][y];
                if(t != null) {
                    if (t.getPosition() != positions[x][y]) {
                        movingTiles.add(t);
                        t.move(positions[x][y]);
                    }
                }
            }
        }
        if (movingTiles.isEmpty()) {
            isMoving = false;
        }
        else
            spawnNeeded = true;
    }


    public void spawn(){
        if(spawnNeeded){
            addRandom();
            spawnNeeded = false;
        }
    }

    public void finishedMoving(Tile t) {
    //finish moving is false only if all tiles are at their place
        movingTiles.remove(t);
        if (movingTiles.isEmpty()) {
            isMoving = false;
            spawn();
        }
    }

}
