package com.zhengshuo.phoenix.ui.chat.weight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.util.CommonUtils;

/**
 * Voice recorder view
 *
 */
public class FastVoiceRecorderView extends RelativeLayout {
    protected Context context;
    protected LayoutInflater inflater;
    protected Drawable[] micImages;
    protected FastVoiceRecorder voiceRecorder;

    protected PowerManager.WakeLock wakeLock;
    protected ImageView ivIcon;
    protected ImageView micImage;
    protected TextView recordingHint;

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // change image
            int index = msg.what;
            if (index < 0 || index > micImages.length - 1) {
                return;
            }
            micImage.setImageDrawable(micImages[index]);
        }
    };

    public FastVoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public FastVoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastVoiceRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.widget_voice_recorder, this);

        ivIcon = findViewById(R.id.iv_icon);
        micImage = findViewById(R.id.mic_image);
        recordingHint = findViewById(R.id.recording_hint);

        voiceRecorder = new FastVoiceRecorder(micImageHandler);

        // animation resources, used for recording
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.huatongtiao1),
                getResources().getDrawable(R.drawable.huatongtiao2),
                getResources().getDrawable(R.drawable.huatongtiao3),
                getResources().getDrawable(R.drawable.huatongtiao4)};

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
    }

    /**
     * on speak button touched
     * 
     * @param v
     * @param event
     */
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event, EaseVoiceRecorderCallback recorderCallback) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            try {
                FastChatRowVoicePlayer voicePlayer = FastChatRowVoicePlayer.getInstance(context);
                if (voicePlayer.isPlaying())
                    voicePlayer.stop();
                v.setPressed(true);
                setTextContent(v, true);
                startRecording();
            } catch (Exception e) {
                v.setPressed(false);
            }
            return true;
        case MotionEvent.ACTION_MOVE:
            if (event.getY() < dip2px(getContext(), 10)) {
                setTextContent(v, false);
                showReleaseToCancelHint();
            } else {
                setTextContent(v, true);
                showMoveUpToCancelHint();
            }
            return true;
        case MotionEvent.ACTION_UP:
            v.setPressed(false);
            setTextContent(v, false);
            if (event.getY() < 0) {
                // discard the recorded audio.
                discardRecording();
            } else {
                // stop recording and send voice file
                try {
                    int length = stopRecoding();
                    if (length > 0) {
                        if (recorderCallback != null) {
                            recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                        }
                    } else if (length == 401) {
                        Toast.makeText(context, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        default:
            discardRecording();
            return false;
        }
    }

    private void setTextContent(View view, boolean pressed) {
        if(view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
            View child = ((ViewGroup) view).getChildAt(0);
            if(child instanceof TextView) {
                ((TextView) child).setText(getContext().getString(pressed ? R.string.button_pushtotalk_pressed : R.string.button_pushtotalk));
            }
        }
    }

    public interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         * 
         * @param voiceFilePath
         *            录音完毕后的文件路径
         * @param voiceTimeLength
         *            录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

    /**
     * 开始录音
     */
    public void startRecording() {
        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            recordingHint.setText(context.getString(R.string.move_up_to_cancel));
            recordingHint.setBackgroundResource(R.drawable.shape_ing_voice);
            ivIcon.setImageResource(R.mipmap.maikefeng);
            micImage.setVisibility(VISIBLE);
            voiceRecorder.startRecording(context);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void showReleaseToCancelHint() {
        recordingHint.setText(context.getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.shape_cancel_voice);
        ivIcon.setImageResource(R.mipmap.chehui_yuyin);
        micImage.setVisibility(GONE);
    }

    public void showMoveUpToCancelHint() {
        recordingHint.setText(context.getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.shape_ing_voice);
        ivIcon.setImageResource(R.mipmap.maikefeng);
        micImage.setVisibility(VISIBLE);
    }

    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                this.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public int stopRecoding() {
        this.setVisibility(View.INVISIBLE);
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }

    public String getVoiceFileName() {
        return voiceRecorder.getVoiceFileName();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

    /**
     * dip to px
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
