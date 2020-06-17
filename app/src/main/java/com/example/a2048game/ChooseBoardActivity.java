package com.example.a2048game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChooseBoardActivity extends AppCompatActivity {

    private int boardsIndex;
    private int modesIndex;
    private int exponent;

    private boolean secondViewIsVisible;
    private boolean thirdViewIsVisible;

    private ArrayList<BoardType> currentDisplayedBoards;
    private ArrayList<BoardType> squareBoards = new ArrayList<>();
    private ArrayList<BoardType> rectangleBoards = new ArrayList<>();

    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;

    private  Animation rightInAnim ;
    private Animation leftInAnim ;
    private Animation rightOutAnim ;
    private Animation leftOutAnim ;

    MusicService.HomeWatcher mHomeWatcher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_board_layout);

        SharedPreferences sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        if (!sp.getBoolean("mute_music",false)){
            playMusic();
        }

        boardsIndex = 0;
        modesIndex = 0;
        exponent = 2;
        secondViewIsVisible = false;
        thirdViewIsVisible = false;

        setModeSelecting();
        initBoardsArrays();
        setBoardSelecting();
        setExponentSelecting();

        firstLayout = findViewById(R.id.select_mode_layout);
        secondLayout = findViewById(R.id.select_board_layout);
        thirdLayout = findViewById(R.id.select_exponent_layout);

        Animation scaleAnim = AnimationUtils.loadAnimation(ChooseBoardActivity.this,R.anim.scale_anim);
        rightInAnim = AnimationUtils.loadAnimation(ChooseBoardActivity.this,R.anim.slide_in_right);
        leftInAnim = AnimationUtils.loadAnimation(ChooseBoardActivity.this,R.anim.slide_in_left);
        rightOutAnim = AnimationUtils.loadAnimation(ChooseBoardActivity.this,R.anim.slide_out_right);
        leftOutAnim = AnimationUtils.loadAnimation(ChooseBoardActivity.this,R.anim.slide_out_left);

        Button btnNextFirst = findViewById(R.id.btn_next_mode);
        btnNextFirst.setAnimation(scaleAnim);
        btnNextFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) { }
                    public void onAnimationRepeat(Animation animation) { }
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

        Button btnNextSecond = findViewById(R.id.btn_next_board_type);
        btnNextSecond.setAnimation(scaleAnim);
        btnNextSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) { }
                    public void onAnimationRepeat(Animation animation) { }
                    public void onAnimationEnd(Animation animation) {
                        secondLayout.setVisibility(View.GONE);
                        thirdLayout.startAnimation(rightInAnim);
                        thirdLayout.setVisibility(View.VISIBLE);
                        thirdViewIsVisible = true;
                    }
                });
                secondLayout.startAnimation(leftOutAnim);

            }
        });

        Button btnPlay = findViewById(R.id.btn_play);
        btnPlay.setAnimation(scaleAnim);
        setExponentSelecting();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(ChooseBoardActivity.this, GameActivity.class);
                intent.putExtra("rows", currentDisplayedBoards.get(boardsIndex).rows);
                intent.putExtra("cols", currentDisplayedBoards.get(boardsIndex).cols);
                intent.putExtra("exponent", exponent);
                intent.putExtra("game_mode",modesIndex);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        playClick();
        if (thirdViewIsVisible) {
            rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    thirdLayout.setVisibility(View.GONE);
                    secondLayout.setVisibility(View.VISIBLE);
                    secondLayout.startAnimation(leftInAnim);
                    thirdViewIsVisible = false;
                }
            });
            thirdLayout.startAnimation(rightOutAnim);
        }
        else if (secondViewIsVisible) {
            rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) { }
                public void onAnimationRepeat(Animation animation) { }
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


        boardType = new BoardType(4,4,getDrawable(R.drawable.pic_board_4x4));
        squareBoards.add(boardType);
        boardType = new BoardType(5,5,getDrawable(R.drawable.pic_board_5x5));
        squareBoards.add(boardType);
        boardType = new BoardType(6,6,getDrawable(R.drawable.pic_board_6x6));
        squareBoards.add(boardType);
        boardType = new BoardType(3,3,getDrawable(R.drawable.pic_board_3x3));
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
    private void setModeSelecting(){
        final ArrayList<Drawable> modeTypes = new ArrayList<>();
        final ArrayList<String> modeNames = new ArrayList<>();
        final ArrayList<String> modeExp = new ArrayList<>();

        modeTypes.add(getDrawable(R.drawable.pic_classic_mode));
        modeNames.add(getString(R.string.mode_classic));
        modeExp.add(getString(R.string.mode_exp_classic));
        modeTypes.add(getDrawable(R.drawable.pic_block_mode));
        modeNames.add(getString(R.string.mode_blocks));
        modeExp.add(getString(R.string.mode_exp_blocks));
        modeTypes.add(getDrawable(R.drawable.anim_mode_classic));
        modeNames.add(getString(R.string.mode_shuffle));
        modeExp.add(getString(R.string.mode_exp_shuffle));

        final TextView tvModeExp = findViewById(R.id.tv_mode_exp);
        final TextView tvModeName = findViewById(R.id.tv_mode_type);
        final ImageView ivMode = findViewById(R.id.iv_select_mode);
        ivMode.setImageDrawable(modeTypes.get(0));

        final ImageButton btnLeft = findViewById(R.id.btn_left_mode);
        ImageButton btnRight = findViewById(R.id.btn_right_mode);
        AnimationDrawable animationDrawable = (AnimationDrawable) modeTypes.get(2);
        animationDrawable.start();

        btnRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playClick();
                if (modesIndex == 2){
                    modesIndex =0;
                }else {
                    modesIndex ++;
                }
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivMode.setImageDrawable(modeTypes.get(modesIndex));
                        tvModeName.setText (modeNames.get(modesIndex));
                        tvModeExp.setText(modeExp.get(modesIndex));
                        ivMode.startAnimation(rightInAnim);
                    }
                });
                ivMode.startAnimation(leftOutAnim);

                }
        });
        btnLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playClick();
                if (modesIndex == 0){
                    modesIndex =2;
                } else {
                    modesIndex --;
                }
                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivMode.setImageDrawable(modeTypes.get(modesIndex));
                        tvModeName.setText (modeNames.get(modesIndex));
                        tvModeExp.setText(modeExp.get(modesIndex));
                        ivMode.startAnimation(leftInAnim);
                    }
                });
                ivMode.startAnimation(rightOutAnim);
            }
        });


    }
    private void setBoardSelecting(){

        currentDisplayedBoards = squareBoards;
        final TextView tvBoardType = findViewById(R.id.tv_shape_type);
        final ImageView ivBoardType = findViewById(R.id.image_view);
        ivBoardType.setImageDrawable(currentDisplayedBoards.get(0).drawable);
        tvBoardType.setText(currentDisplayedBoards.get(0).getTypeString());
        final ImageButton btnLeft = findViewById(R.id.btn_left_board_type);
        ImageButton btnRight = findViewById(R.id.btn_right_board_type);


        final RadioGroup shapeRadioGroup = findViewById(R.id.rg_board_shape);
        final RadioButton radioButtonRectangle = findViewById(R.id.rb_rectangle);
        final RadioButton radioButtonSquare = findViewById(R.id.rb_square);
        radioButtonSquare.setTextColor(Color.rgb(90,85,83));
        radioButtonRectangle.setTextColor(Color.rgb(167,168,168));



        shapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                playClick();
                if (checkedId == radioButtonRectangle.getId()){
                    boardsIndex = 0;
                    currentDisplayedBoards = rectangleBoards;
                    tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    radioButtonRectangle.setTextColor(Color.rgb(90,85,83));
                    radioButtonSquare.setTextColor(Color.rgb(167,168,168));

                } else if (checkedId == radioButtonSquare.getId()){
                    boardsIndex = 0;
                    currentDisplayedBoards = squareBoards;
                    tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    radioButtonSquare.setTextColor(Color.rgb(90,85,83));
                    radioButtonRectangle.setTextColor(Color.rgb(167,168,168));
                }
            }
        });


        btnLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playClick();
                if (boardsIndex == 0){
                    boardsIndex = currentDisplayedBoards.size()-1;
                }
                else { boardsIndex--; }
                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                        ivBoardType.startAnimation(leftInAnim);
                        tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                    }
                });
                ivBoardType.startAnimation(rightOutAnim);

            }
        });
        btnRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playClick();
                if (boardsIndex == currentDisplayedBoards.size() - 1){
                    boardsIndex = 0;
                }
                else { boardsIndex++; }
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                        ivBoardType.startAnimation(rightInAnim);
                        tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                    }
                });
                ivBoardType.startAnimation(leftOutAnim);


            }
        });


    }
    private void setExponentSelecting(){
        final ArrayList<String> exponentExp = new ArrayList<>();
        exponentExp.add(getString(R.string.exponent_exp_2));
        exponentExp.add(getString(R.string.exponent_exp_3));
        exponentExp.add(getString(R.string.exponent_exp_4));
        exponentExp.add(getString(R.string.exponent_exp_5));

        final RadioGroup rgExponentTop = findViewById(R.id.rg_exponent_top);
        final RadioGroup rgExponentBottom = findViewById(R.id.rg_exponent_bottom);
        final RadioButton rbExponent2 = findViewById(R.id.rb_exponent_2);
        final RadioButton rbExponent3 = findViewById(R.id.rb_exponent_3);
        final RadioButton rbExponent4 = findViewById(R.id.rb_exponent_4);
        final RadioButton rbExponent5 = findViewById(R.id.rb_exponent_5);
        final TextView tvExponentExp = findViewById(R.id.tv_exponent_exp);

       rbExponent2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               playClick();
               rbExponent2.setChecked(true);
               rgExponentBottom.clearCheck();
               exponent = 2;
               tvExponentExp.setText(exponentExp.get(0));

           }
       });
        rbExponent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                rbExponent3.setChecked(true);
                rgExponentBottom.clearCheck();
                exponent = 3;
                tvExponentExp.setText(exponentExp.get(1));

            }
        });
        rbExponent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                rbExponent4.setChecked(true);
                rgExponentTop.clearCheck();
                exponent = 4;
                tvExponentExp.setText(exponentExp.get(2));

            }
        });
        rbExponent5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                rbExponent5.setChecked(true);
                rgExponentTop.clearCheck();
                exponent = 5;
                tvExponentExp.setText(exponentExp.get(3));
            }
        });

    }


    /////////////////////// Music and sound methods////////////////////////



    private void playClick() {
        SharedPreferences sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        final MediaPlayer click = MediaPlayer.create(ChooseBoardActivity.this, R.raw.button_click);
        if (!sp.getBoolean("mute_sounds", false)) {
            click.start();
        }
    }
    private void playMusic() {
        //BIND Music Service
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        //Start HomeWatcher
        mHomeWatcher = new MusicService.HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new MusicService.HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

    }

    //Bind/Unbind music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };
    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        //Detect idle screen
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isInteractive();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }

    static class BoardType {

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
}

//////////////////////////////////////////



