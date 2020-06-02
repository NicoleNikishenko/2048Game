package com.example.a2048game.Tiles;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.example.a2048game.MainActivity;
import com.example.a2048game.R;

import java.util.ArrayList;


public class BitmapCreator {

    private Context context = MainActivity.getContext();
    public static int cellDefaultHeight;
    public static int cellDefaultWidth;
    public static int exponent;

    private final Paint mPaint = new Paint();
    private Rect textBounds = new Rect();
    private static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();


    public int getCellDefaultHeight() {
        return cellDefaultHeight;
    }

    public int getCellDefaultWidth() {
        return cellDefaultWidth;
    }


    public Drawable createDrawable (int index){
        Drawable drawable = MainActivity.getContext().getDrawable(R.drawable.cell_rectangle);
        if (drawable != null) {
            switch (index) {
                case 0:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value2), PorterDuff.Mode.SRC_OVER);
                    break;
                case 1:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value4), PorterDuff.Mode.SRC_OVER);
                    break;
                case 2:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value8), PorterDuff.Mode.SRC_OVER);
                    break;
                case 3:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value16), PorterDuff.Mode.SRC_OVER);
                    break;
                case 4:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value32), PorterDuff.Mode.SRC_OVER);
                    break;
                case 5:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value64), PorterDuff.Mode.SRC_OVER);
                    break;
                case 6:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value128), PorterDuff.Mode.SRC_OVER);
                    break;
                case 7:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value256), PorterDuff.Mode.SRC_OVER);
                    break;
                case 8:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value512), PorterDuff.Mode.SRC_OVER);
                    break;
                case 9:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value1024), PorterDuff.Mode.SRC_OVER);
                    break;
                case 10:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value2048), PorterDuff.Mode.SRC_OVER);
                    break;
                default:
                    drawable.setColorFilter(context.getResources().getColor(R.color.valueOther), PorterDuff.Mode.SRC_OVER);
                    break;
            }
        }
        return drawable;
    }

    public void createBitmap(int index) {
        //get drawable
        Drawable drawable = createDrawable(index);
        int value = (int)Math.pow(exponent,index+1);
        String text = Integer.toString(value);
        Bitmap bitmap = Bitmap.createBitmap(cellDefaultWidth,cellDefaultHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //set text style and size
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(true);
        int textSize = 60;
        mPaint.setTextSize(textSize);
        drawable.setBounds(0,0,cellDefaultWidth,cellDefaultHeight);
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        while (textBounds.width() > cellDefaultWidth-20 && textSize >= 10) {
            textSize -= 10;
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(text, 0, text.length(), textBounds); mPaint.getTextBounds(text, 0, text.length(), textBounds);
        }
        //draw bitmap
        drawable.draw(canvas);
        canvas.drawText(text, (float)cellDefaultWidth/2- textBounds.exactCenterX(), (float)cellDefaultHeight/2 - textBounds.exactCenterY(), mPaint);
        bitmapArrayList.add(bitmap);
    }


    public Bitmap getBitmap (int value){
        //calculating index according to value
        int index = (int)( Math.log(value) /Math.log(exponent) ) - 1 ;

        //first create 12 starting tiles
        if(bitmapArrayList.isEmpty()) {
            for(int i = 0 ; i < 12 ; i++) {
                createBitmap(i);
            }
        }
        //if tile not created yet, create it
        if (index == bitmapArrayList.size()){
            createBitmap(index);
        }

        return bitmapArrayList.get(index);
    }


}
