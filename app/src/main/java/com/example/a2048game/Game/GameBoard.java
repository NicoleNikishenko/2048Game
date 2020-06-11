package com.example.a2048game.Game;

import android.graphics.Canvas;

import com.example.a2048game.Tiles.Position;
import com.example.a2048game.Tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class GameBoard {


    private Tile[][] tempBoard;
    private Tile[][] board;
    private Tile[][] oldBoard;
    private Position[][] positions;
    private int boardRows;
    private int boardCols;
    private int exponent;

    Random rand = new Random();

    private boolean isMoving = false;
    private boolean spawnNeeded = false;
    private boolean canUndo;
    private boolean boardIsInitialized;
    private ArrayList<Tile> movingTiles;

    private boolean gameOver = false;

    private GameView callback;
    private long currentScore;
    private long oldScore;



    //constructor
    public GameBoard(int rows, int cols, int exponentValue, GameView callback) {
        exponent = exponentValue;
        boardRows = rows;
        boardCols = cols;
        board = new Tile[rows][cols];
        positions = new Position[rows][cols];
        this.callback = callback;
        currentScore = 0;
        boardIsInitialized = false;

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
        if(!boardIsInitialized) {
            addRandom();
            addRandom();
            movingTiles = new ArrayList<>();
            boardIsInitialized = true;
        }
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
        saveBoardState();
        if (!isMoving) {
           isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = x - 1; k >= 0; k--) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y];
                                if (tempBoard[k + 1][y] == board[x][y]) {
                                    tempBoard[k + 1][y] = null;
                                }
                            } else if (tempBoard[k][y].getValue() == board[x][y].getValue() && tempBoard[k][y].notAlreadyIncreased()) {
                                tempBoard[k][y] = board[x][y];
                                tempBoard[k][y].setIncreased(true);
                                if (tempBoard[k + 1][y] == board[x][y]) {
                                    tempBoard[k + 1][y] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }


    void down(){
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = boardRows-1 ; x >= 0; x--) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = x + 1; k < boardRows; k++) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y];
                                if (tempBoard[k - 1][y] == board[x][y]) {
                                    tempBoard[k - 1][y] = null;
                                }
                            } else if (tempBoard[k][y].getValue() == board[x][y].getValue() && tempBoard[k][y].notAlreadyIncreased()) {
                                tempBoard[k][y] = board[x][y];
                                tempBoard[k][y].setIncreased(true);
                                if (tempBoard[k - 1][y] == board[x][y]) {
                                    tempBoard[k - 1][y] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }


    void left() {
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = y - 1; k >= 0; k--) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y];
                                if (tempBoard[x][k + 1] == board[x][y]) {
                                    tempBoard[x][k + 1] = null;
                                }
                            } else if (tempBoard[x][k].getValue() == board[x][y].getValue() && tempBoard[x][k].notAlreadyIncreased()) {
                                tempBoard[x][k] = board[x][y];
                                tempBoard[x][k].setIncreased(true);
                                if (tempBoard[x][k + 1] == board[x][y]) {
                                    tempBoard[x][k + 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }


    void right(){
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = boardCols-1 ; y >= 0; y--) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = y + 1; k < boardCols; k++) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y];
                                if (tempBoard[x][k - 1] == board[x][y]) {
                                    tempBoard[x][k - 1] = null;
                                }
                            } else if (tempBoard[x][k].getValue() == board[x][y].getValue() && tempBoard[x][k].notAlreadyIncreased()) {
                                tempBoard[x][k] = board[x][y];
                                tempBoard[x][k].setIncreased(true);
                                if (tempBoard[x][k - 1] == board[x][y]) {
                                    tempBoard[x][k - 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }


    public void moveTiles(){
        //checking which tiles changed position and moving them accordingly
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                Tile t = tempBoard[x][y];
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
        //checking if there's no more moves
        gameOver = true;
            for (int x = 0; x < boardRows; x++) {  //this loop will check if there are any empty tiles
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] == null) {
                        gameOver = false;
                        break; //will jump out of the loop
                    }
                }
            }
            if (gameOver) {  //this loop will check if there are any neighbors who can be merged
                for (int x = 0; x < boardRows; x++) {
                    for (int y = 0; y < boardCols; y++) {
                        if ((x > 0 && board[x - 1][y].getValue() == board[x][y].getValue()) ||
                                (x < boardRows-1 && board[x + 1][y].getValue() == board[x][y].getValue()) ||
                                (y > 0 && board[x][y - 1].getValue() == board[x][y].getValue()) ||
                                (y < boardCols-1 && board[x][y + 1].getValue() == board[x][y].getValue())) {
                            gameOver = false;
                            break;
                        }
                    }
                }
            }
        }



        public void updateScore(long value){
        currentScore += value;
        callback.updateScore(currentScore);
    }



    public void saveBoardState(){
        //saving board state for undo functionality
        canUndo = true;
        oldBoard = new Tile[boardRows][boardCols];
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if(board[x][y] != null) {
                    oldBoard[x][y] = board[x][y].copyTile();
                }
            }
        }
        oldScore = currentScore;
    }

    public void undoMove(){
        //undo last move
        if(canUndo) {
            board = oldBoard;
            currentScore = oldScore;
            callback.updateScore(currentScore);
            canUndo = false;
        }
    }

    public void resetGame(){
        //reset the game and score
        gameOver = false;
        canUndo = false;

        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                board[x][y] = null;
            }
        }
        currentScore = 0;
        callback.updateScore(currentScore);
        addRandom();
        addRandom();
    }


}
