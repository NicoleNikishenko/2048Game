package com.example.a2048game;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.a2048game.Game.MainThread;
import com.example.a2048game.Tiles.BitmapCreator;

import java.util.ArrayList;
import java.util.Objects;


public class GameActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView scoreTv;
    private TextView topScoreTv;

    MusicService.HomeWatcher mHomeWatcher;
    private SharedPreferences sp;


    private int boardRows;
    private int boardCols;
    private int boardExponent;
    private int gameMode;
    private boolean isTutorialFromMainScreen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContext = this;
        boardRows = getIntent().getIntExtra("rows",4);
        boardCols = getIntent().getIntExtra("cols",4);
        boardExponent = getIntent().getIntExtra("exponent",2);
        gameMode = getIntent().getIntExtra("game_mode",0);
        isTutorialFromMainScreen = getIntent().getBooleanExtra("tutorial", false);

        setContentView(R.layout.game_layout);

        sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        if (!sp.getBoolean("mute_music",false)){
            playMusic();
        }
        changeLayoutParams();

        ImageButton homeBtn = findViewById(R.id.btn_home);
        homeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(GameActivity.this,HomeActivity.class);
                startActivity(intent);
                destroyGameThread();
            }
        });

        ImageButton btnSetting = findViewById(R.id.btn_settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                openSettingsDialog();
            }
        });

        ImageButton btnScoreBoard = findViewById(R.id.btn_Score_board);
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                openScoreBoardDialog();
            }
        });

    }



    //Getters and Setters
    public static Context getContext(){
        return mContext;
    }
    public int getBoardRows() { return boardRows; }
    public int getBoardCols() { return boardCols; }
    public int getBoardExponent() { return boardExponent; }
    public int getGameMode(){return gameMode;}
    public boolean isTutorial(){
        SharedPreferences sharedPreferences = getSharedPreferences("play_history",MODE_PRIVATE);

        if(!sharedPreferences.getBoolean("tutorial_played",false) || isTutorialFromMainScreen){
            sharedPreferences.edit().putBoolean("tutorial_played",true).apply();
            return true;
        }
        return false;
    }
    public boolean isTutorialFromMainScreen(){return isTutorialFromMainScreen;}
    public boolean isSoundPlayed(){
        return !sp.getBoolean("mute_sounds", false);
    }
    private void changeLayoutParams(){
        //change layout size according to rows and cols
        LinearLayout layout = findViewById(R.id.game_layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = params.height = Resources.getSystem().getDisplayMetrics().widthPixels;
        double difference = (double) boardCols / boardRows;

        if (boardRows == 3 && boardCols == 3){
            params.width = params.height = (int)(params.width * 0.8);
        }
       if (boardRows == 4 && boardCols == 4){
           params.width = params.height = (int)(params.width * 0.85);
       }
        if (boardRows != boardCols) {
            params.width = params.height = (int)(params.width * 1.1);
            params.width = (int) (params.width * difference);
        }
       layout.setLayoutParams(params);
   }
    public void updateScore(final long score, final long topScore){
        this.scoreTv = findViewById(R.id.tv_current_score);
        this.topScoreTv = findViewById(R.id.tv_best_score);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(score == 0) {
                    scoreTv.setText(getString(R.string.start));
                } else {
                    scoreTv.setText(String.valueOf(score));
                }
                topScoreTv.setText(String.valueOf(topScore));
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
        playClick();
        super.onBackPressed();
        Intent intent = new Intent(GameActivity.this,HomeActivity.class);
        startActivity(intent);
        destroyGameThread();
    }

    // Dialogs
    private void openSettingsDialog(){

        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setContentView(R.layout.setting_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        final RadioGroup rgMusic = dialog.findViewById(R.id.rg_music_select);
        final RadioButton musicOn = dialog.findViewById(R.id.rb_music_on);
        final RadioButton musicOff = dialog.findViewById(R.id.rb_music_off);


        if (!sp.getBoolean("mute_music",false)){
            rgMusic.check(musicOn.getId());
            musicOn.setTextColor(Color.rgb(90,85,83));
        } else {
            rgMusic.check(musicOff.getId());
            musicOff.setTextColor(Color.rgb(90,85,83));
        }



        rgMusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                playClick();
                if (checkedId == musicOn.getId()) {
                    musicOn.setTextColor(Color.rgb(90,85,83));
                    musicOff.setTextColor(Color.rgb(167,168,168));
                    if (mServ != null) {
                        mServ.startMusic();
                    } else { playMusic();}
                    sp.edit().putBoolean("mute_music",false).apply();

                } else {
                    musicOff.setTextColor(Color.rgb(90,85,83));
                    musicOn.setTextColor(Color.rgb(167,168,168));
                    if (mServ != null ) {
                        mServ.stopMusic();
                    }
                    sp.edit().putBoolean("mute_music",true).apply();

                }
            }
        });


        RadioGroup rgSound = dialog.findViewById(R.id.rg_sound_select);
        final RadioButton soundOn = dialog.findViewById(R.id.rb_sound_on);
        final RadioButton soundOff = dialog.findViewById(R.id.rb_sound_off);

        if (!sp.getBoolean("mute_sounds",false)){
            rgSound.check(soundOn.getId());
            soundOn.setTextColor(Color.rgb(90,85,83));
        } else {
            rgSound.check(soundOff.getId());
            soundOff.setTextColor(Color.rgb(90,85,83));
        }



        rgSound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == soundOn.getId()) {
                    sp = getSharedPreferences("music_settings", MODE_PRIVATE);
                    sp.edit().putBoolean("mute_sounds",false).apply();
                    playClick();
                    soundOn.setTextColor(Color.rgb(90,85,83));
                    soundOff.setTextColor(Color.rgb(167,168,168));

                } else {
                    sp.edit().putBoolean("mute_sounds",true).apply();
                    soundOff.setTextColor(Color.rgb(90,85,83));
                    soundOn.setTextColor(Color.rgb(167,168,168));
                }
            }
        });

        Animation scaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_anim);
        Button closeBtn = dialog.findViewById(R.id.btn_close);
        closeBtn.startAnimation(scaleAnim);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
            }
        });



    }
    private void openScoreBoardDialog(){
        SharedPreferences sharedPreferences = getSharedPreferences("2048Project", MODE_PRIVATE);
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setContentView(R.layout.scoreboard_layout);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ListView listView = dialog.findViewById(R.id.lv_score_board);
        ArrayList<ScoreBoardBuilder.Score> classicScores = ScoreBoardBuilder.createClassicArrayList(sharedPreferences,"0");
        ArrayList<ScoreBoardBuilder.Score> blocksScores = ScoreBoardBuilder.createClassicArrayList(sharedPreferences,"1");
        ArrayList<ScoreBoardBuilder.Score> shuffleScores = ScoreBoardBuilder.createClassicArrayList(sharedPreferences,"2");
        final ScoreBoardBuilder.ScoreAdapter scoreAdapterClassic = new ScoreBoardBuilder.ScoreAdapter(classicScores,this);
        final ScoreBoardBuilder.ScoreAdapter scoreAdapterBlocks = new ScoreBoardBuilder.ScoreAdapter(blocksScores,this);
        final ScoreBoardBuilder.ScoreAdapter scoreAdapterShuffle = new ScoreBoardBuilder.ScoreAdapter(shuffleScores,this);
        listView.setAdapter(scoreAdapterClassic);

        final Animation rightInAnim = AnimationUtils.loadAnimation(GameActivity.this,R.anim.slide_in_right);
        final Animation leftInAnim = AnimationUtils.loadAnimation(GameActivity.this,R.anim.slide_in_left);
        final Animation rightOutAnim = AnimationUtils.loadAnimation(GameActivity.this,R.anim.slide_out_right);
        final Animation  leftOutAnim = AnimationUtils.loadAnimation(GameActivity.this,R.anim.slide_out_left);

        Animation scaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_anim);
        final TextView currentModeTv = dialog.findViewById(R.id.tv_mode_type);
        final int[] index = {0};

        ImageButton btnRight = dialog.findViewById(R.id.btn_right_mode);
        btnRight.startAnimation(scaleAnim);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index[0] == 2) {
                    index [0] = 0;
                } else { index[0] ++; }
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        switch(index[0]){
                            case 0:
                                playClick();
                                listView.setAdapter(scoreAdapterClassic);
                                currentModeTv.setText(getString(R.string.mode_classic));
                                break;
                            case 1:
                                playClick();
                                listView.setAdapter(scoreAdapterBlocks);
                                currentModeTv.setText(getString(R.string.mode_blocks));
                                break;
                            case 2:
                                playClick();
                                listView.setAdapter(scoreAdapterShuffle);
                                currentModeTv.setText(getString(R.string.mode_shuffle));
                                break;
                        }
                        listView.startAnimation(rightInAnim);
                    }
                });
                listView.startAnimation(leftOutAnim);



            }
        });

        ImageButton btnLeft = dialog.findViewById(R.id.btn_left_mode);
        btnLeft.startAnimation(scaleAnim);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index[0] == 0 ) {
                    index [0] = 2;
                } else { index[0] --; }

                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        switch(index[0]){
                            case 0:
                                playClick();
                                listView.setAdapter(scoreAdapterClassic);
                                currentModeTv.setText(getString(R.string.mode_classic));
                                break;
                            case 1:
                                playClick();
                                listView.setAdapter(scoreAdapterBlocks);
                                currentModeTv.setText(getString(R.string.mode_blocks));
                                break;
                            case 2:
                                playClick();
                                listView.setAdapter(scoreAdapterShuffle);
                                currentModeTv.setText(getString(R.string.mode_shuffle));
                                break;
                        }
                        listView.startAnimation(leftInAnim);
                    }
                });
                listView.startAnimation(rightOutAnim);


            }
        });

        Button btnClose = dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
            }
        });
        dialog.show();

    }




////////////////////////////////// Music Service


    private void playClick() {
        final MediaPlayer click = MediaPlayer.create(GameActivity.this, R.raw.button_click);
        if (isSoundPlayed()) {
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
                Scon,Context.BIND_AUTO_CREATE);
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



}








