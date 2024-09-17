package com.example.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class AudioAssistantReceiver extends BroadcastReceiver {

    private TextToSpeech tts;
    private String message;  // メッセージを保持

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.example.alarmclock.ACTION_PLAY_MESSAGE".equals(intent.getAction())) {
            message = intent.getStringExtra("MESSAGE");  // メッセージを保存

            if (tts == null) {
                tts = new TextToSpeech(context, status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.JAPANESE);
                        setUtteranceListener();  // リスナーを設定
                        speak(message);  // 初期化後に読み上げ
                    } else {
                        Log.e("alarmclockdd", "TextToSpeech initialization failed");
                    }
                });
                Log.d("alarmclockdd", "TTS Initialized");
            } else {
                speak(message);  // 既に初期化されている場合は直接読み上げ
            }
        }
    }

    private void setUtteranceListener() {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d("alarmclockdd", "Utterance Started: " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d("alarmclockdd", "Utterance Completed: " + utteranceId);
                if (message.equals("勉強しましょう")) {
                    Log.d("alarmclockdd", "onDone: 勉強しましょう 完了");
                    //stopService("")
                }
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("alarmclockdd", "Error during Utterance: " + utteranceId);
            }
        });
    }

    private void speak(String message) {
        if (tts != null) {
            if (tts.isSpeaking()) {
                tts.stop();  // 既に読み上げ中なら停止
            }
            Log.d("alarmclockdd", "Speaking message: " + message);
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "UniqueUtteranceID");
        }
    }

    @Override
    public void finalize() throws Throwable {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.finalize();
    }
}
