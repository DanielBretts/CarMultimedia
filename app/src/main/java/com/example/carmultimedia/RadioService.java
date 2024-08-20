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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        String streamLink = intent.getStringExtra("STREAM_LINK");

        if ("ACTION_PLAY".equals(action)) {
            startPlaying(streamLink);
        } else if ("ACTION_PAUSE".equals(action)) {
            pausePlaying();
        } else if ("ACTION_STOP".equals(action)) {
            stopPlaying();
        }

        return START_NOT_STICKY;
    }

    private void startPlaying(String streamLink) {
        stopPlaying(); // Stop any current playback before starting a new one

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(streamLink);
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pausePlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
