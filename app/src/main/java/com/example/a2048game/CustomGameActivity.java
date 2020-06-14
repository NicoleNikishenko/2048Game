package com.example.a2048game;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CustomGameActivity extends AppCompatActivity {

    private int index;
    private int exponent;
    private boolean secondViewIsVisible;
    private ArrayList<BoardType> currentDisplayedBoards;
    private ArrayList<BoardType> squareBoards = new ArrayList<>();
    private ArrayList<BoardType> rectangleBoards = new ArrayList<>();
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;

    private  Animation rightInAnim ;
    private Animation leftInAnim ;
    private Animation rightOutAnim ;
    private Animation leftOutAnim ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_board_layout);

        index = 0;
        exponent = 2;
        secondViewIsVisible = false;

        initBoardsArrays();
        setBoardSelecting();
        setExponentSelecting();

        firstLayout = findViewById(R.id.first_layout);
        secondLayout = findViewById(R.id.second_layout);
        Animation scaleAnim = AnimationUtils.loadAnimation(CustomGameActivity.this,R.anim.scale_anim);
        rightInAnim = AnimationUtils.loadAnimation(CustomGameActivity.this,R.anim.slide_in_right);
        leftInAnim = AnimationUtils.loadAnimation(CustomGameActivity.this,R.anim.slide_in_left);
        rightOutAnim = AnimationUtils.loadAnimation(CustomGameActivity.this,R.anim.slide_out_right);
        leftOutAnim = AnimationUtils.loadAnimation(CustomGameActivity.this,R.anim.slide_out_left);

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setAnimation(scaleAnim);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        firstLayout.setVisibility(View.GONE);
                        secondLayout.startAnimation(rightInAnim);
                        secondLayout.setVisibility(View.VISIBLE);
                        secondViewIsVisible = true;
                    }
                });
                firstLayout.startAnimation(leftOutAnim);

            }
        });

        Button btnPlay = findViewById(R.id.btn_play);
        btnPlay.setAnimation(scaleAnim);
        setExponentSelecting();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomGameActivity.this, MainActivity.class);
                intent.putExtra("rows", currentDisplayedBoards.get(index).rows);
                intent.putExtra("cols", currentDisplayedBoards.get(index).cols);
                intent.putExtra("exponent", exponent);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (secondViewIsVisible){
            rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationRepeat(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    secondLayout.setVisibility(View.GONE);
                    firstLayout.setVisibility(View.VISIBLE);
                    firstLayout.startAnimation(leftInAnim);
                    secondViewIsVisible = false;
                }
            });
            secondLayout.startAnimation(rightOutAnim);
        } else {
            super.onBackPressed();
        }
    }
    private void initBoardsArrays(){
        BoardType boardType;

        boardType = new BoardType(3,3,getDrawable(R.drawable.pic_board_3x3));
        squareBoards.add(boardType);
        boardType = new BoardType(4,4,getDrawable(R.drawable.pic_board_4x4));
        squareBoards.add(boardType);
        boardType = new BoardType(5,5,getDrawable(R.drawable.pic_board_5x5));
        squareBoards.add(boardType);
        boardType = new BoardType(6,6,getDrawable(R.drawable.pic_board_6x6));
        squareBoards.add(boardType);

        boardType = new BoardType(4,3,getDrawable(R.drawable.pic_board_3x4));
        rectangleBoards.add(boardType);
        boardType = new BoardType(5,3,getDrawable(R.drawable.pic_board_3x5));
        rectangleBoards.add(boardType);
        boardType = new BoardType(5,4,getDrawable(R.drawable.pic_board_4x5));
        rectangleBoards.add(boardType);
        boardType = new BoardType(6,5,getDrawable(R.drawable.pic_board_5x6));
        rectangleBoards.add(boardType);

    }
    private void setBoardSelecting(){

        currentDisplayedBoards = squareBoards;
        final TextView tvBoardType = findViewById(R.id.tv_shape_type);
        final ImageView ivBoardType = findViewById(R.id.image_view);
        ivBoardType.setImageDrawable(currentDisplayedBoards.get(0).drawable);
        final ImageButton btnLeft = findViewById(R.id.btn_left);
        ImageButton btnRight = findViewById(R.id.btn_right);


        final RadioGroup shapeRadioGroup = findViewById(R.id.rg_board_shape);
        final RadioButton radioButtonRectangle = findViewById(R.id.rb_rectangle);
        final RadioButton radioButtonSquare = findViewById(R.id.rb_square);

        shapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButtonRectangle.getId()){
                    index = 0;
                    currentDisplayedBoards = rectangleBoards;
                    tvBoardType.setText(currentDisplayedBoards.get(index).getTypeString());
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(index).drawable);

                } else if (checkedId == radioButtonSquare.getId()){
                    index = 0;
                    currentDisplayedBoards = squareBoards;
                    tvBoardType.setText(currentDisplayedBoards.get(index).getTypeString());
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(index).drawable);
                }
            }
        });


        btnLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (index == 0){
                    index = currentDisplayedBoards.size()-1;
                }
                else { index--; }
                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivBoardType.setImageDrawable(currentDisplayedBoards.get(index).drawable);
                        ivBoardType.startAnimation(leftInAnim);
                        tvBoardType.setText(currentDisplayedBoards.get(index).getTypeString());
                    }
                });
                ivBoardType.startAnimation(rightOutAnim);

            }
        });
        btnRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (index == currentDisplayedBoards.size() - 1){
                    index = 0;
                }
                else { index++; }
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivBoardType.setImageDrawable(currentDisplayedBoards.get(index).drawable);
                        ivBoardType.startAnimation(rightInAnim);
                        tvBoardType.setText(currentDisplayedBoards.get(index).getTypeString());
                    }
                });
                ivBoardType.startAnimation(leftOutAnim);


            }
        });


    }
    private void setExponentSelecting(){

        final RadioGroup rgExponentTop = findViewById(R.id.rg_exponent_top);
        final RadioGroup rgExponentBottom = findViewById(R.id.rg_exponent_bottom);
        final RadioButton rbExponent2 = findViewById(R.id.rb_exponent_2);
        final RadioButton rbExponent3 = findViewById(R.id.rb_exponent_3);
        final RadioButton rbExponent4 = findViewById(R.id.rb_exponent_4);
        final RadioButton rbExponent5 = findViewById(R.id.rb_exponent_5);


       rbExponent2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               rbExponent2.setChecked(true);
               rgExponentBottom.clearCheck();
               exponent = 2;

           }
       });
        rbExponent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbExponent3.setChecked(true);
                rgExponentBottom.clearCheck();
                exponent = 3;

            }
        });
        rbExponent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbExponent4.setChecked(true);
                rgExponentTop.clearCheck();
                exponent = 4;

            }
        });
        rbExponent5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbExponent5.setChecked(true);
                rgExponentTop.clearCheck();
                exponent = 5;
            }
        });

    }

}

class BoardType {

    public int rows;
    public int cols;
    public String typeString;
    public Drawable drawable;

    public BoardType(int rows, int cols, Drawable drawable) {
        this.rows = rows;
        this.cols = cols;
        this.drawable = drawable;
        typeString = rows + "x" + cols;
    }
    public String getTypeString() {
        return typeString;
    }
}



