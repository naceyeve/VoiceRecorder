package com.ilike.voice.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ilike.voice.R;
import com.ilike.voice.adapter.EaseMessageAdapter;
import com.ilike.voicerecorder.VoiceRecorderServiceIMP;
import com.ilike.voicerecorder.model.MessageBean;
import com.ilike.voicerecorder.widget.VoiceRecorderView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class TestVoiceActivity extends AppCompatActivity {

    protected VoiceRecorderView voiceRecorderView;
    protected ListView message_list;
    protected TextView tvRecorder;


    private List<MessageBean> voices;
    EaseMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        voices = new ArrayList<>();
        initView();
        getPermissions();
    }

    //运行授权，6.0以上系统需要
    private void getPermissions() {
        RxPermissions rxPermissions = new RxPermissions(TestVoiceActivity.this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        if (value) {
                            Toast.makeText(TestVoiceActivity.this, "同意权限", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TestVoiceActivity.this, "拒绝权限", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initView() {




        message_list = findViewById(R.id.message_list);

        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (VoiceRecorderView) findViewById(R.id.voice_recorder);


        voiceRecorderView.setShowMoveUpToCancelHint("松开手指，取消发送");
        voiceRecorderView.setShowReleaseToCancelHint("手指上滑，取消发送");
        tvRecorder = (TextView) findViewById(R.id.tv_touch_recorder);
        VoiceRecorderServiceIMP.getInstance(TestVoiceActivity.this).setVoiceRecorderView(voiceRecorderView);
        VoiceRecorderServiceIMP.getInstance(TestVoiceActivity.this).bindTouchView(tvRecorder, new VoiceRecorderServiceIMP.VoiceRecorderCompleteListener() {
            @Override
            public void Complete(MessageBean bean) {
                voices.add(bean);
                adapter.notifyDataSetChanged();
            }
        });


        adapter = new EaseMessageAdapter(this, voices);
        message_list.setAdapter(adapter);
        adapter.setOnItemClickLister(new EaseMessageAdapter.onItemClickLister() {
            @Override
            public void onItemClick(ImageView imageView, String path, int position) {
                //播放语音
                //  VoicePlayClickListener voicePlayClickListener = new VoicePlayClickListener(imageView, TestVoiceActivity.this);
               /* voicePlayClickListener.setStopPlayIcon(R.drawable.ease_chatto_voice_playing);
                voicePlayClickListener.setPlayingIconDrawableResoure(R.drawable.voice_to_icon);*/
                //   voicePlayClickListener.playVoice(path);

                // new VoicePlayClickListener(imageView, TestVoiceActivity.this).playUrlVoice("http://img.layuva.com//b96c4bde124a328d9c6edb5b7d51afb2.amr");



                VoiceRecorderServiceIMP.getInstance(TestVoiceActivity.this).playVoice(imageView,path);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceRecorderServiceIMP.getInstance(TestVoiceActivity.this).release();
    }
}
