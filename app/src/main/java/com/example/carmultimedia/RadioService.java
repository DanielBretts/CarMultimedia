package com.example.carmultimedia;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class RadioService extends Service {

    private MediaPlayer mediaPlayer;
    private boolean isPrepared = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initializeMediaPlayer(String streamLink) {
        if (mediaPlayer != null) {
            mediaPlayer.release();  // Release the existing MediaPlayer
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(streamLink);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                mp.start();
            });
        } catch (IOException e) {
            Log.e("RadioService", "Error preparing media player", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        String streamLink = intent.getStringExtra("STREAM_LINK");

        if (streamLink != null) {
            if ("ACTION_PLAY".equals(action)) {
                initializeMediaPlayer(streamLink);
            } else if ("ACTION_PAUSE".equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } else if ("ACTION_STOP".equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    isPrepared = false;
                    stopSelf();
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
