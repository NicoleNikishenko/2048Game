package com.example.a2048game;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a2048game.Game.GameView;
import com.example.a2048game.Game.MainThread;
import com.example.a2048game.Tiles.BitmapCreator;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView scoreTv;
    private TextView topScoreTv;


    // pro tip - rows have to be bigger then cols
    private int boardRows;
    private int boardCols;
    private int boardExponent;

    GameView gameView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        boardRows = getIntent().getIntExtra("rows",4);
        boardCols = getIntent().getIntExtra("cols",4);
        boardExponent = getIntent().getIntExtra("exponent",2);



        setContentView(R.layout.activity_main);

        changeLayoutParams();

        this.scoreTv = findViewById(R.id.tv_current_score);
        this.topScoreTv = findViewById(R.id.tv_best_score);

        ImageButton btnNewGame = findViewById(R.id.btn_home);
        btnNewGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
                destroyGameThread();
            }
        });


    }



    //Getters and Setters
    public static Context getContext(){
        return mContext;
    }
    public int getBoardRows() {
        return boardRows;
    }
    public int getBoardCols() {
        return boardCols;
    }
    public int getBoardExponent() {
        return boardExponent;
    }
    public void setBoardType(int boardCols ,int boardRows ,int boardExponent) {
        this.boardCols = boardCols;
        this.boardRows = boardRows;
        this.boardExponent = boardExponent;
    }

   private void changeLayoutParams(){
        //change layout size according to rows and cols
        LinearLayout layout = findViewById(R.id.game_layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        double difference = (double) boardCols / boardRows;

        if (boardRows == 3 && boardCols == 3){
            params.width = params.height = (int)(params.width * 0.8);
        }
       if (boardRows == 4 && boardCols == 4){
           params.width = params.height = (int)(params.width * 0.9);
       }

        if (boardRows != boardCols) {
            params.width = params.height = (int)(params.width * 1.1);
            params.width = (int) (params.width * difference);
            layout.setLayoutParams(params);
        }
   }

    public void updateScore(final long score, final long topScore){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(score == 0) {
                    scoreTv.setText("START!");
                } else {
                    scoreTv.setText(Long.toString(score));
                }
                topScoreTv.setText(Long.toString(topScore));
            }
        });

    }



    //Destroying thread on back pressed

    private MainThread thread;
    public void setThread(MainThread thread) {
        this.thread = thread;
    }

    public void destroyGameThread(){
        super.onBackPressed();
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                BitmapCreator bitmapCreator = new BitmapCreator();
                bitmapCreator.clearBitmapArray();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        destroyGameThread();
    }
}






