package com.example.alarmclock.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import com.example.alarmclock.util.DatabaseHelper;
import com.example.alarmclock.listcomponent.ListAdapter;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationActivity extends AppCompatActivity {

    private DatabaseHelper helper = null;
    final static public int NEW_REQ_CODE = 1;
    final static public int EDIT_REQ_CODE = 2;
    RecyclerView rv = null;
    RecyclerView.Adapter adapter = null;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_main);

        // アラームのデータを取得
        ArrayList<ListItem> data = this.loadAlarms();

        // RecyclerViewに設定
        this.setRV(data);

        // フローティングアクションボタンの設定
        FloatingActionButton fbt = findViewById(R.id.fbtn);
        fbt.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent i = new Intent(ConfirmationActivity.this, InputActivity.class);
                        i.putExtra(getString(R.string.request_code),NEW_REQ_CODE);
                        startActivityForResult(i,NEW_REQ_CODE);
                    }
                });

        // 欲しいパーミッションを配列に格納
        String[] permissions = {
                android.Manifest.permission.SCHEDULE_EXACT_ALARM, // Android 12+ 正確なアラーム
                //android.Manifest.permission.ACCESS_FINE_LOCATION, // 位置情報
                //android.Manifest.permission.CAMERA // カメラ
                android.Manifest.permission.USE_EXACT_ALARM,
                android.Manifest.permission.WAKE_LOCK,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                android.Manifest.permission.PACKAGE_USAGE_STATS,
                android.Manifest.permission.WRITE_SETTINGS
        };

        // パーミッションをリクエスト
        requestNecessaryPermissions(permissions);

        if (!checkReadStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private boolean checkReadStatsPermission() {
        // AppOpsManagerを取得
        AppOpsManager aom = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        // GET_USAGE_STATSのステータスを取得
        int mode = aom.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            // AppOpsの状態がデフォルトなら通常のpermissionチェックを行う。
            // 普通のアプリならfalse
            return checkPermission("android.permission.PACKAGE_USAGE_STATS", android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
        }
        // AppOpsの状態がデフォルトでないならallowedのみtrue
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // パーミッションのリクエストを行う関数
    private void requestNecessaryPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsToRequest = new ArrayList<>();

            // 許可されていないパーミッションを確認
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }

            // リクエストする必要があるパーミッションがある場合のみリクエスト
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(
                        this,
                        permissionsToRequest.toArray(new String[0]),
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }
    // リクエスト結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // パーミッションが許可された場合の処理
                    // 例: 許可された権限のログ出力
                    System.out.println("Permission granted: " + permissions[i]);
                } else {
                    // パーミッションが拒否された場合の処理
                    System.out.println("Permission denied: " + permissions[i]);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // リクエストコードと結果コードをチェック
        if(requestCode == RESULT_CANCELED){
            // 何もしない
        }else if((requestCode == NEW_REQ_CODE || requestCode == EDIT_REQ_CODE) && resultCode == RESULT_OK){
            ArrayList<ListItem> dataAlarms = this.loadAlarms();
            this.updateRV(dataAlarms);
        }
    }

    private ArrayList<ListItem> loadAlarms(){
        helper = DatabaseHelper.getInstance(this);

        ArrayList<ListItem> data = new ArrayList<>();

        try(SQLiteDatabase db = helper.getReadableDatabase()) {

            // 必要な列を追加
            String[] cols ={"alarmid","name","alarttime", "activitytype", "studytime"};

            Cursor cs = db.query("alarms", cols, null, null,
                    null, null, "alarmid", null);
            boolean eol = cs.moveToFirst();
            while (eol){
                ListItem item = new ListItem();
                item.setAlarmID(cs.getInt(0));
                item.setAlarmName(cs.getString(1));
                item.setTime(cs.getString(2));
                item.setActivityType(cs.getString(3)); // 朝活の種類
                item.setStudyTime(cs.getInt(4));   // 勉強時間
                data.add(item);
                eol = cs.moveToNext();
            }
        }
        return data;
    }

    private void setRV(ArrayList<ListItem> data){
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new ListAdapter(data);
        rv.setAdapter(adapter);
    }

    private void updateRV(ArrayList<ListItem> data){
        adapter = new ListAdapter(data);
        rv.setAdapter(adapter);
        // rv.swapAdapter(adapter,false);
    }
}