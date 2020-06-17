package com.example.a2048game;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;



public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();

    MediaPlayer mPlayer;

    private int length = 0;

    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.sound_track); //replace with your song name!
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(0.3f, 0.3f);
        }


        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer != null) {
            mPlayer.start();
        }
        return START_NOT_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                length = mPlayer.getCurrentPosition();
            }
        }
    }

    public void resumeMusic() {
        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {
                mPlayer.seekTo(length);
                mPlayer.start();
            }
        }
    }

    public void startMusic() {
        mPlayer = MediaPlayer.create(this, R.raw.sound_track);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(0.2f, 0.2f);
            mPlayer.start();
        }

    }

    public void stopMusic() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }

    public static class HomeWatcher {
        //static final String TAG = "hg";
        private Context mContext;
        private IntentFilter mFilter;
        private OnHomePressedListener mListener;
        private InnerRecevier mRecevier;

        public HomeWatcher(Context context) {
            mContext = context;
            mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        }

        public void setOnHomePressedListener(OnHomePressedListener listener) {
            mListener = listener;
            mRecevier = new InnerRecevier();
        }

        public void startWatch() {
            if (mRecevier != null) {
                mContext.registerReceiver(mRecevier, mFilter);
            }
        }

        public void stopWatch() {
            if (mRecevier != null) {
                mContext.unregisterReceiver(mRecevier);
            }
        }

        class InnerRecevier extends BroadcastReceiver {
            final String SYSTEM_DIALOG_REASON_KEY = "reason";
            final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
            final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        //Log.e(TAG, "action:" + action + ",reason:" + reason);
                        if (mListener != null) {
                            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                                mListener.onHomePressed();
                            } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                                mListener.onHomeLongPressed();
                            }
                        }
                    }
                }
            }
        }

        public interface OnHomePressedListener {
            void onHomePressed();

            void onHomeLongPressed();
        }
    }
}