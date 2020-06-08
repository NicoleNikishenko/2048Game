package com.example.a2048game;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;

import androidx.appcompat.app.AlertDialog;

import com.example.a2048game.Tiles.Position;
import com.example.a2048game.Tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class GameBoard {


    private Tile[][] board;
    private Tile[][] newBoard;
    private Position[][] positions;
    private int boardRows;
    private int boardCols;
    private int exponent;

    Random rand = new Random();

    private boolean isMoving = false;
    private boolean spawnNeeded = false;
    private ArrayList<Tile> movingTiles;

    private boolean gameOver = false;

    //constructor
    public GameBoard(int rows, int cols, int exponentValue) {
        exponent = exponentValue;
        boardRows = rows;
        boardCols = cols;
        board = new Tile[rows][cols];
        positions = new Position[rows][cols];
    }


    //getters and setters
    public boolean isGameOver(){ return gameOver; }
    public int getExponent() { return exponent; }
    public int getRows() { return boardRows; }
    public int getCols() { return boardCols; }
    public Tile getTile(int x, int y) { return board[x][y]; }
    public void setPositions (int matrixX ,int matrixY ,int positionX,int positionY){
        Position position = new Position(positionX,positionY);
        positions[matrixX][matrixY]= position;
    }



    public void initBoard(){

        //initializing board with 2 random tiles
//        addRandom();
//        addRandom();



        board[0][0] = new Tile(2,positions[0][0],this);
        board[0][1] = new Tile(4,positions[0][1],this);
        board[0][2] = new Tile(2,positions[0][2],this);
        board[0][3] = new Tile(4,positions[0][3],this);
        board[1][0] = new Tile(4,positions[1][0],this);
        board[1][1] = new Tile(2,positions[1][1],this);
        board[1][2] = new Tile(4,positions[1][2],this);
        board[1][3] = new Tile(2,positions[1][3],this);
        board[2][0] = new Tile(2,positions[2][0],this);
        board[2][1] = new Tile(4,positions[2][1],this);
        board[2][2] = new Tile(2,positions[2][2],this);
        board[2][3] = new Tile(4,positions[2][3],this);
        board[3][0] = new Tile(4,positions[3][1],this);
        board[3][1] = new Tile(2,positions[3][2],this);
        board[3][2] = new Tile(4,positions[3][3],this);


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
                        board[x][y] = new Tile(exponent,positions[x][y],this);
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
        boolean updating = false;
            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        board[x][y].update();

                        if(board[x][y].needsToUpdate())
                            updating = true;
                    }
                }
            }
        if (!updating){
            checkIfGameOver();
        }
    }



    void up(){
        if (!isMoving) {
           isMoving = true;
            newBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        newBoard[x][y] = board[x][y];
                        for (int k = x - 1; k >= 0; k--) {
                            if (newBoard[k][y] == null) {
                                newBoard[k][y] = board[x][y];
                                if (newBoard[k + 1][y] == board[x][y]) {
                                    newBoard[k + 1][y] = null;
                                }
                            } else if (newBoard[k][y].getValue() == board[x][y].getValue() && newBoard[k][y].notAlreadyIncreased()) {
                                newBoard[k][y] = board[x][y];
                                newBoard[k][y].setIncreased(true);
                                if (newBoard[k + 1][y] == board[x][y]) {
                                    newBoard[k + 1][y] = null;
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

            for (int x = boardRows-1 ; x >= 0; x--) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        newBoard[x][y] = board[x][y];
                        for (int k = x + 1; k < boardRows; k++) {
                            if (newBoard[k][y] == null) {
                                newBoard[k][y] = board[x][y];
                                if (newBoard[k - 1][y] == board[x][y]) {
                                    newBoard[k - 1][y] = null;
                                }
                            } else if (newBoard[k][y].getValue() == board[x][y].getValue() && newBoard[k][y].notAlreadyIncreased()) {
                                newBoard[k][y] = board[x][y];
                                newBoard[k][y].setIncreased(true);
                                if (newBoard[k - 1][y] == board[x][y]) {
                                    newBoard[k - 1][y] = null;
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

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        newBoard[x][y] = board[x][y];
                        for (int k = y - 1; k >= 0; k--) {
                            if (newBoard[x][k] == null) {
                                newBoard[x][k] = board[x][y];
                                if (newBoard[x][k + 1] == board[x][y]) {
                                    newBoard[x][k + 1] = null;
                                }
                            } else if (newBoard[x][k].getValue() == board[x][y].getValue() && newBoard[x][k].notAlreadyIncreased()) {
                                newBoard[x][k] = board[x][y];
                                newBoard[x][k].setIncreased(true);
                                if (newBoard[x][k + 1] == board[x][y]) {
                                    newBoard[x][k + 1] = null;
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

            for (int x = 0; x < boardRows; x++) {
                for (int y = boardCols-1 ; y >= 0; y--) {
                    if (board[x][y] != null) {
                        newBoard[x][y] = board[x][y];
                        for (int k = y + 1; k < boardCols; k++) {
                            if (newBoard[x][k] == null) {
                                newBoard[x][k] = board[x][y];
                                if (newBoard[x][k - 1] == board[x][y]) {
                                    newBoard[x][k - 1] = null;
                                }
                            } else if (newBoard[x][k].getValue() == board[x][y].getValue() && newBoard[x][k].notAlreadyIncreased()) {
                                newBoard[x][k] = board[x][y];
                                newBoard[x][k].setIncreased(true);
                                if (newBoard[x][k - 1] == board[x][y]) {
                                    newBoard[x][k - 1] = null;
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
        //if board did'nt changes take no action, else new spawn is needed
        if (movingTiles.isEmpty()) {
            isMoving = false;
        }
        else
            spawnNeeded = true;
    }


    public void spawn(){
        //spawning new random only after finishMoving is complete
        if(spawnNeeded){
            addRandom();
            spawnNeeded = false;
        }
    }

    public void finishedMoving(Tile t) {
    //finish moving is false only if all tiles are at their right place
        movingTiles.remove(t);
        if (movingTiles.isEmpty()) {
            isMoving = false;
            spawn();
        }
    }

    public void checkIfGameOver() {

        gameOver = true;

        for (int i = 0; i < 4; i++) {  //this loop will check if there are any empty tiles
            for (int j = 0; j < 4; j++) {

                if (board[i][j] == null) {
                    gameOver = false;
                    break; //will jump out of the loop
                }
            }
        }
        if (gameOver) {  //this loop will check if there are any neighbors who can be merged
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {

                    if ((i > 0 && board[i - 1][j].getValue() == board[i][j].getValue()) ||
                            (i < 3 && board[i + 1][j].getValue() == board[i][j].getValue()) ||
                            (j > 0 && board[i][j - 1].getValue() == board[i][j].getValue()) ||
                            (j < 3 && board[i][j + 1].getValue() == board[i][j].getValue())) {
                        gameOver = false;
                        break;
                    }
                }
            }
        }
    }


}
