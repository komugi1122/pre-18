package com.example.memorycollection;
import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {

    private static MediaPlayer mediaPlayer;

    // BGMを開始するメソッド
    public static void playBGM(Context context, int resourceId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, resourceId);
        mediaPlayer.setLooping(true);  // ループ設定
        mediaPlayer.start();  // 再生開始
    }

    // BGMを停止するメソッド
    public static void stopBGM() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

