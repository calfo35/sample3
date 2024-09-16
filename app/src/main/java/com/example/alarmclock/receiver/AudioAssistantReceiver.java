package com.example.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class AudioAssistantReceiver extends BroadcastReceiver implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.example.alarmclock.ACTION_PLAY_MESSAGE".equals(intent.getAction())) {
            String message = intent.getStringExtra("MESSAGE");
            if (tts == null) {
                tts = new TextToSpeech(context, status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.JAPANESE);
                        speak(context, message);
                    } else {
                        Log.e("alarmclockdd", "TextToSpeech initialization failed");
                    }
                });
                Log.e("alarmclockdd", "syokika");
            } else {
                speak(context, message);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.JAPANESE);

        }
    }

    private void speak(Context context, String message) {
        if (tts != null) {
            /*if (tts.isSpeaking()) {
                // 読み上げ中なら停止
                tts.stop();
            }

             */
            Log.d("alarmclockdd", "why speak?");
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
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
