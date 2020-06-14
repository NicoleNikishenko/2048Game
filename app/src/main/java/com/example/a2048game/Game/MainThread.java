package com.example.a2048game.Game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;

    public MainThread (SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView =  gameView;
    }

    public void setRunning (boolean isRunning) {
        running = isRunning;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder){
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run() {
        long startTime,timeMillis,waitTime;
        int targetFPS = 60;
        long targetTime = 1000/ targetFPS;

        while (running){
            startTime = System.nanoTime();
            Canvas canvas = null;

            try {
                canvas =  surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update();
                    gameView.draw(canvas);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if( canvas !=  null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() -startTime)/100000;
            waitTime =  targetTime - timeMillis;

            try {
                if (waitTime > 0) {
                    sleep(waitTime);

                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

