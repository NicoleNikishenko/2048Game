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
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.a2048game.Game.MainThread;
import com.example.a2048game.Tiles.BitmapCreator;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView scoreTv;
    private TextView topScoreTv;

    HomeWatcher mHomeWatcher;
    private SharedPreferences sp;


    private int boardRows;
    private int boardCols;
    private int boardExponent;
    private int gameMode;
    private boolean isTutorialNeeded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        boardRows = getIntent().getIntExtra("rows",4);
        boardCols = getIntent().getIntExtra("cols",4);
        boardExponent = getIntent().getIntExtra("exponent",2);
        gameMode = getIntent().getIntExtra("game_mode",0);
        isTutorialNeeded = getIntent().getBooleanExtra("tutorial", false);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        if (!sp.getBoolean("mute_music",false)){
            playMusic();
        }
        changeLayoutParams();

        ImageButton btnNewGame = findViewById(R.id.btn_home);
        btnNewGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
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

        
    }



    //Getters and Setters
    public static Context getContext(){
        return mContext;
    }
    public int getBoardRows() { return boardRows; }
    public int getBoardCols() { return boardCols; }
    public int getBoardExponent() { return boardExponent; }
    public int getGameMode(){return gameMode;}
    public boolean isTutorial(){ return isTutorialNeeded;}
    public boolean isSoundMuted(){
        if (sp.getBoolean("mute_sounds", false)) {
            return true;
        } else {
            return false;
        }
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
                    scoreTv.setText("START!");
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
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        destroyGameThread();
    }

    private void openSettingsDialog(){

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.setting_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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


        Button btn = dialog.findViewById(R.id.btn_close);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
            }
        });



    }


////////////////////////////////// Music Service


    private void playClick() {
        final MediaPlayer click = MediaPlayer.create(MainActivity.this, R.raw.button_click);
        if (!isSoundMuted()) {
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
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
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
            isScreenOn = pm.isScreenOn();
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

    public void showLeaderBoard(){

        Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
        intent.putExtra("game_mode",gameMode);
        startActivity(intent);
        destroyGameThread();

    }

}








