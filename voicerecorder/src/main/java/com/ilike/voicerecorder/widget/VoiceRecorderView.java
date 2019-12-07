/**
 * Copyright (C) 2017 ilikeshatang. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ilike.voicerecorder.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ilike.voicerecorder.R;


/**
 * desc:   Voice recorder view
 * author: wangshanhai
 * email: ilikeshatang@gmail.com
 * date: 2017/10/31 15:27
 */
public class VoiceRecorderView extends RelativeLayout {
    protected Context context;
    protected LayoutInflater inflater;
    protected Drawable[] micImages;
    protected boolean isImagesCustom = false;


    protected ImageView micImage;
    protected TextView recordingHint;

    protected String release_to_cancel = "";
    protected String move_up_to_cancel = "";



    public VoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_voice_recorder, this);

        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);



        // animation resources, used for recording
        micImages = new Drawable[]{
                getResources().getDrawable(R.drawable.ease_record_animate_01),
                getResources().getDrawable(R.drawable.ease_record_animate_02),
                getResources().getDrawable(R.drawable.ease_record_animate_03),
                getResources().getDrawable(R.drawable.ease_record_animate_04),
                getResources().getDrawable(R.drawable.ease_record_animate_05),
                getResources().getDrawable(R.drawable.ease_record_animate_06),
                getResources().getDrawable(R.drawable.ease_record_animate_07),
                getResources().getDrawable(R.drawable.ease_record_animate_08),
                getResources().getDrawable(R.drawable.ease_record_animate_09),
                getResources().getDrawable(R.drawable.ease_record_animate_10),
                getResources().getDrawable(R.drawable.ease_record_animate_11),
                getResources().getDrawable(R.drawable.ease_record_animate_12),
                getResources().getDrawable(R.drawable.ease_record_animate_13),
                getResources().getDrawable(R.drawable.ease_record_animate_14),};



        //默认提示语
        release_to_cancel = context.getString(R.string.release_to_cancel);
        move_up_to_cancel = context.getString(R.string.move_up_to_cancel);

    }

    public void fingerDown() {
        this.setVisibility(View.VISIBLE);
        recordingHint.setText(context.getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
    }

    public void changeVoiceUI(int position) {
        micImage.setImageDrawable(micImages[position]);

    }

    public void fingerUp() {
        this.setVisibility(View.INVISIBLE);
    }




    public void showReleaseToCancelHint() {
       /* recordingHint.setText(context.getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);*/
        recordingHint.setText(release_to_cancel);
        recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);
    }

    public void showMoveUpToCancelHint() {
        recordingHint.setText(move_up_to_cancel);
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
    }


    /**
     * 目前需要传入15张帧动画png
     *
     * @param animationDrawable
     */
    public void setDrawableAnimation(Drawable[] animationDrawable) {
        micImages = null;
        this.micImages = animationDrawable;
    }


    /**
     * 设置按下显示的提示
     *
     * @param releaseToCancelHint
     */
    public void setShowReleaseToCancelHint(String releaseToCancelHint) {
        this.release_to_cancel = releaseToCancelHint;
    }

    /**
     * 设置手指向上移动显示的提示语
     *
     * @param moveUpToCancelHint
     */
    public void setShowMoveUpToCancelHint(String moveUpToCancelHint) {
        this.move_up_to_cancel = moveUpToCancelHint;
    }

}
