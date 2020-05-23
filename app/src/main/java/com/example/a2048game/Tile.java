package com.example.a2048game;

public class Tile {
    public int value;
    private int x;
    private int y;

    public Tile(int value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
