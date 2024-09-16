package com.example.alarmclock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.alarmclock.R;
import com.example.alarmclock.activity.WakeUpActivity;

import java.util.List;

// 参考 https://github.com/hiroaki-dev/AlarmSample/blob/master/app/src/main/java/me/hiroaki/alarmsample/PlaySoundService.java

public class CheckSmartphoneService extends Service{

    private static final String CHANNEL_ID = "WakeupServiceChannel";

    private static final long INTERVAL = 1000; // 1秒ごとにチェック
    private Handler handler = new Handler();
    private Runnable runnable;

    public CheckSmartphoneService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startMonitoringUsage();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("アプリ")
                .setContentText("バックグラウンドです")
                .setSmallIcon(R.drawable.ic_launcher_background) // アイコンを適切なものに置き換えてください
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, WakeUpActivity.class), PendingIntent.FLAG_MUTABLE))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Start the service in the foreground
        startForeground(2, notification);


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

    private void startMonitoringUsage() {
        runnable = new Runnable() {
            @Override
            public void run() {

                checkUsageStats();
                handler.postDelayed(this, INTERVAL);
            }
        };
        handler.post(runnable);
    }
    private void checkUsageStats() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long end = System.currentTimeMillis();
        long start = end - INTERVAL;

        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);

        if (stats != null && !stats.isEmpty()) {
            UsageStats recentStats = stats.get(stats.size() - 1);
            String packageName = recentStats.getPackageName();
            if (!TextUtils.equals(packageName, getPackageName())) {
                // スマホ全体で操作が検出された場合、画面の明るさを最小にする
                setScreenBrightnessToMinimum();

            }else{
                resetScreenBrightness();
            }
        }
    }

    // 画面の明るさを設定するメソッド
    private void setScreenBrightnessToMinimum() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    // 明るさの設定を変更
                    Settings.System.putInt(
                            getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS,
                            1 // 明るさを最小に設定 (0-255 の範囲)
                    );
                    Log.d("alarmclockdd","check monitor");
                } else {
                    // 書き込み権限がない場合
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            Log.e("alarmclockdd", "Error setting screen brightness", e);
        }
    }

    private void resetScreenBrightness() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    // デフォルトの明るさに戻す
                    Settings.System.putInt(
                            getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS,
                            255 // デフォルトの明るさに設定 (0-255 の範囲)
                    );
                }
            }
        } catch (Exception e) {
            Log.e("alarmclockdd", "Error resetting screen brightness", e);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        resetScreenBrightness(); // サービス終了時に画面の明るさをデフォルトに戻す
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private WindowManager getWindow() {
        return (WindowManager) getSystemService(WINDOW_SERVICE);
    }
}