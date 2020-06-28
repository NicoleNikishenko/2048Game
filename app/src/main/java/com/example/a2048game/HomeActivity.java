package com.example.a2048game;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;



public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sp;
    MusicService.HomeWatcher mHomeWatcher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);

        sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        if (!sp.getBoolean("mute_music",false)){
            playMusic();
        }



        Animation scaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_anim);

        Button btnNewGame = findViewById(R.id.btn_new_game);
        Button btnLoadGame = findViewById(R.id.btn_load_game);
        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnScoreBoard = findViewById(R.id.btn_Score_board);
        btnNewGame.startAnimation(scaleAnim);
        btnLoadGame.startAnimation(scaleAnim);
        btnSettings.startAnimation(scaleAnim);
        btnScoreBoard.startAnimation(scaleAnim);
        btnLoadGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(HomeActivity.this, ChooseBoardActivity.class);
                startActivity(intent);
            }
        });

        btnLoadGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                intent.putExtra("tutorial", true);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                openSettingsDialog();
            }
        });

        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                openScoreBoardDialog();
            }
        });


    }

    // Dialogs

    private void openScoreBoardDialog(){
        SharedPreferences sharedPreferences = getSharedPreferences("2048Project", MODE_PRIVATE);
        final Dialog dialog = new Dialog(HomeActivity.this);
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

        final Animation rightInAnim = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.slide_in_right);
        final Animation leftInAnim = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.slide_in_left);
        final Animation rightOutAnim = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.slide_out_right);
        final Animation  leftOutAnim = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.slide_out_left);


        Animation scaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_anim);
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
    private void openSettingsDialog(){


        final Dialog dialog = new Dialog(HomeActivity.this);
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

        Animation scaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_anim);
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


    ////////////////////////////////// Music Service

    private void playClick() {
        SharedPreferences sp = getSharedPreferences("music_settings", MODE_PRIVATE);
        final MediaPlayer click = MediaPlayer.create(HomeActivity.this, R.raw.button_click);
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
