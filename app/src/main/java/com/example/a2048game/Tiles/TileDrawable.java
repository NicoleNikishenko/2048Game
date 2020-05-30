package com.example.a2048game.Tiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.example.a2048game.R;

public class TileDrawable {

    private static Context context;
    private static Drawable cellRectangle;
    private static int cellDefaultHeight;
    private static int cellDefaultWidth;



    public void setContext (Context context) {
        TileDrawable.context = context;
        cellRectangle = context.getResources().getDrawable(R.drawable.cell_rectangle);
    }


    public int getCellDefaultHeight() {
        return cellDefaultHeight;
    }

    public int getCellDefaultWidth() {
        return cellDefaultWidth;
    }

    public static void setCellDefaultHeight(int cellDefaultHeight) {
        TileDrawable.cellDefaultHeight = cellDefaultHeight;
    }

    public static void setCellDefaultWidth(int cellDefaultWidth) {
        TileDrawable.cellDefaultWidth = cellDefaultWidth;
    }

    public Drawable createDrawable (int value){

        Drawable drawable = cellRectangle;

        switch (value){
            case 2:
                drawable.setColorFilter(context.getResources().getColor(R.color.value2), PorterDuff.Mode.SRC_OVER);
                break;
            case 4:
                drawable.setColorFilter(context.getResources().getColor(R.color.value4), PorterDuff.Mode.SRC_OVER);
                break;
            case 8:
                drawable.setColorFilter(context.getResources().getColor(R.color.value8), PorterDuff.Mode.SRC_OVER);
                break;
            case 16:
                drawable.setColorFilter(context.getResources().getColor(R.color.value16), PorterDuff.Mode.SRC_OVER);
                break;
            case 32:
                drawable.setColorFilter(context.getResources().getColor(R.color.value32), PorterDuff.Mode.SRC_OVER);
                break;
            case 64:
                drawable.setColorFilter(context.getResources().getColor(R.color.value64), PorterDuff.Mode.SRC_OVER);
                break;
            case 128:
                drawable.setColorFilter(context.getResources().getColor(R.color.value128), PorterDuff.Mode.SRC_OVER);
                break;
            case 256:
                drawable.setColorFilter(context.getResources().getColor(R.color.value256), PorterDuff.Mode.SRC_OVER);
                break;
            case 512:
                drawable.setColorFilter(context.getResources().getColor(R.color.value512), PorterDuff.Mode.SRC_OVER);
                break;
            case 1024:
                drawable.setColorFilter(context.getResources().getColor(R.color.value1024), PorterDuff.Mode.SRC_OVER);
                break;
            case 2048:
                drawable.setColorFilter(context.getResources().getColor(R.color.value2048), PorterDuff.Mode.SRC_OVER);
                break;
            default:
                drawable.setColorFilter(context.getResources().getColor(R.color.valueOther), PorterDuff.Mode.SRC_OVER);
                break;
        }
        return drawable;
    }

}
