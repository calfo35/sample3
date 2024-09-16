package com.example.alarmclock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.alarmclock.R;
import com.example.alarmclock.activity.WakeUpActivity;

// 参考 https://github.com/hiroaki-dev/AlarmSample/blob/master/app/src/main/java/me/hiroaki/alarmsample/PlaySoundService.java

public class SoundService extends Service implements MediaPlayer.OnCompletionListener{

    MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "SoundServiceChannel";

    public SoundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("アラーム")
                .setContentText("アラームを鳴らします")
                .setSmallIcon(R.drawable.ic_launcher_background) // アイコンを適切なものに置き換えてください
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, WakeUpActivity.class), PendingIntent.FLAG_MUTABLE))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Start the service in the foreground
        startForeground(1, notification);

        // 参考 https://smartomaizu.com/ringtones/sozai/775.html
        mediaPlayer = MediaPlayer.create(this, R.raw.wakeup);
        mediaPlayer.setOnCompletionListener(this);
        play();

        stopSelf();
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sound Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();

        // CheckSmartphoneService を起動
        Intent serviceIntent2 = new Intent(this, CheckSmartphoneService.class);
        startService(serviceIntent2);

        // WakeupService を起動
        Intent serviceIntent3 = new Intent(this, WakeupService.class);
        startService(serviceIntent3);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 再生
    private void play() {
        mediaPlayer.start();
    }

    // 停止
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // 再生が終わる度に音量を上げてループ再生
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        play();
        //stopSelf();
    }
}