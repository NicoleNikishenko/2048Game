package com.example.a2048game.Score;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.a2048game.GameViewCallback;
import com.example.a2048game.R;


import java.util.zip.Inflater;

public class GameOver {

    GameViewCallback callback;
    Context context;

    public GameOver(GameViewCallback callback, Context context) {

        this.callback = callback;
        this.context = context;


    }

    public void showGameOverDialog(){

        Toast.makeText(context, "GameOverDialog", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View dialogView = inflater.inflate(R.layout.gameover_layout, null, false);

        builder.setView(dialogView).setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();



    }


}
