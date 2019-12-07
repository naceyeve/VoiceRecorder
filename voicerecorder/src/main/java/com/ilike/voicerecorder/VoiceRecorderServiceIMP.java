package com.ilike.voicerecorder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ilike.voicerecorder.core.VoiceRecorder;
import com.ilike.voicerecorder.model.MessageBean;
import com.ilike.voicerecorder.service.PlayService;
import com.ilike.voicerecorder.utils.CommonUtils;
import com.ilike.voicerecorder.utils.EMError;
import com.ilike.voicerecorder.utils.TimeUtils;
import com.ilike.voicerecorder.widget.VoiceRecorderView;

public class VoiceRecorderServiceIMP implements VoiceRecorderService {
    PlayServiceConnection mPlayServiceConnection;
    PlayService mPlayService;
    Context mContext;
    VoiceRecorderView mVoiceRecorderView;
    protected PowerManager.WakeLock wakeLock;
    protected VoiceRecorder voiceRecorder;

    private static volatile VoiceRecorderServiceIMP instance;

    public static VoiceRecorderServiceIMP getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceRecorderServiceIMP.class) {
                if (instance == null) {
                    instance = new VoiceRecorderServiceIMP(context);
                }
            }
        }
        return instance;

    }

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // change image
            mVoiceRecorderView.changeVoiceUI(msg.what);
        }
    };

    public VoiceRecorderServiceIMP(Context context) {
        this.mContext = context;
        Intent intent = new Intent();
        intent.setClass(context, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        context.bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "voice");

        voiceRecorder = new VoiceRecorder(micImageHandler);
    }

    @Override
    public void startRecord() {
        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(mContext, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            voiceRecorder.startRecording(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
        }
    }

    @Override
    public int stopRecord() {
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    @Override
    public void disCardRecord() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void playVoice(ImageView imageView, String path) {
        if (mPlayService != null) {
            mPlayService.setImageView(imageView);
            mPlayService.stopPlayVoiceAnimation();
            mPlayService.play(path);
        }
    }

    @Override
    public void release() {
        mContext.unbindService(mPlayServiceConnection);
    }

    public void bindTouchView(View touchView, final VoiceRecorderCompleteListener listener) {
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mPlayService.isPlaying) {
                        mPlayService.stopPlayVoiceAnimation2();
                    }
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        try {
                            mVoiceRecorderView.fingerDown();
                            startRecord();
                        } catch (Exception e) {
                            v.setPressed(false);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() < 0) {
                            mVoiceRecorderView.showReleaseToCancelHint();
                        } else {
                            mVoiceRecorderView.showMoveUpToCancelHint();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        mVoiceRecorderView.fingerUp();
                        if (event.getY() < 0) {
                            // discard the recorded audio.
                            disCardRecord();
                        } else {
                            // stop recording and send voice file
                            try {
                                int length = stopRecord();
                                if (length > 0) {
                                    MessageBean bean = new MessageBean();
                                    bean.path = getVoiceFilePath();
                                    bean.msg = "image";
                                    bean.second = length;
                                    bean.time = TimeUtils.getCurrentTimeInLong();
                                    if (listener != null) {
                                        listener.Complete(bean);
                                    }


                                } else if (length == EMError.FILE_INVALID) {
                                    Toast.makeText(mContext, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    default:
                        disCardRecord();
                        return false;
                }

            }
        });
    }

    public void setVoiceRecorderView(VoiceRecorderView voiceRecorderView) {
        this.mVoiceRecorderView = voiceRecorderView;
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            Log.e("onServiceConnected----", "onServiceConnected");
            mPlayService = playService;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    public interface VoiceRecorderCompleteListener {
        void Complete(MessageBean bean);
    }


    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }
}
