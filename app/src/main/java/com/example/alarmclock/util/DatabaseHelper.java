package com.example.alarmclock.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final private String DBNAME = "alarm.sqlite";
    static final private int VERSION = 1; // バージョン番号を更新
    private static DatabaseHelper sSingleton = null;

    // 参考 シングルトン
    // https://sakura-bird1.hatenablog.com/entry/20130613/1371122200

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }

    private DatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 新しいテーブルの作成
        db.execSQL("CREATE TABLE alarms (" +
                "alarmid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "alarttime TEXT, " +
                "activitytype TEXT, " +  // 新しいカラム
                "studytime INTEGER);"); // 新しいカラム
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS alarms");
        onCreate(db);
    }
}
