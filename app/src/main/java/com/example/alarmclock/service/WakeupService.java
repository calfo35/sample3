package com.example.alarmclock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.alarmclock.R;
import com.example.alarmclock.activity.WakeUpActivity;
import com.example.alarmclock.receiver.AudioAssistantReceiver;

public class WakeupService extends Service {

    private static final String CHANNEL_ID = "WakeupServiceChannel";
    private static final long INTERVAL = 1000; // 1秒ごとにチェック
    private Handler handler = new Handler();
    private Runnable runnable;
    private int secondsElapsed = 0;

    public WakeupService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("alarmclockdd", "sonomama");
        createNotificationChannel();
        startMonitoring();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Wakeup Service")
                .setContentText("サービスがバックグラウンドで動作しています")
                .setSmallIcon(R.drawable.ic_launcher_background) // アイコンを適切なものに置き換えてください
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, WakeUpActivity.class), PendingIntent.FLAG_MUTABLE))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Start the service in the foreground
        startForeground(3, notification);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Wakeup Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startMonitoring() {
        runnable = new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;
                if (secondsElapsed == 5 || secondsElapsed == 10) {
                    sendBroadcastMessage(secondsElapsed);
                }
                handler.postDelayed(this, INTERVAL);
            }
        };
        handler.post(runnable);
    }

    private void sendBroadcastMessage(int secondsElapsed) {
        Intent intent = new Intent(this, AudioAssistantReceiver.class);
        intent.setAction("com.example.alarmclock.ACTION_PLAY_MESSAGE");

        String message = "";
        if (secondsElapsed == 5) {
            message = "おはようございます";
        } else if (secondsElapsed == 10) {
            message = "勉強しましょう";
        }
        intent.putExtra("MESSAGE", message);
        sendBroadcast(intent);
        Log.d("alarmclockdd", "send broadcast");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // ハンドラーのコールバックを削除
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
