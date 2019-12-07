package com.ilike.voicerecorder;

import android.widget.ImageView;

public interface VoiceRecorderService {
    void startRecord();

    int stopRecord();

    void disCardRecord();

    void playVoice(ImageView imageView, String path);

    void release();
}
