package com.zhengshuo.phoenix.ui.chat.weight;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.zhengshuo.phoenix.common.manager.SettingsManager;
import com.zhengshuo.phoenix.util.FileProvider7;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.util.StringUtil;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageVoiceBody;
import java.io.File;
import java.io.IOException;


public class FastChatRowVoicePlayer {
    private static final String TAG = FastChatRowVoicePlayer.class.getSimpleName();

    private static FastChatRowVoicePlayer instance = null;

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private String playingId;

    private MediaPlayer.OnCompletionListener onCompletionListener;
    private final Context baseContext;

    public static FastChatRowVoicePlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (FastChatRowVoicePlayer.class) {
                if (instance == null) {
                    instance = new FastChatRowVoicePlayer(context);
                }
            }
        }
        return instance;
    }

    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * May null, please consider.
     *
     * @return
     */
    public String getCurrentPlayingId() {
        return playingId;
    }

    public void play(final HTMessage msg, final MediaPlayer.OnCompletionListener listener) {
        if (!(msg.getBody() instanceof HTMessageVoiceBody)) return;

        if (mediaPlayer.isPlaying()) {
            stop();
        }

        playingId = msg.getMsgId();
        onCompletionListener = listener;

        try {
            setSpeaker();
            HTMessageVoiceBody voiceBody = (HTMessageVoiceBody) msg.getBody();

            String localPath = voiceBody.getLocalPath();
            LogUtil.e("HTMessageVoiceBody","play>>>>localPath>>>"+localPath);
            Uri voiceUri = null;
            if (!StringUtil.isBlank(localPath)) {
                File file = new File(localPath);
                voiceUri = FileProvider7.getUriForFile(baseContext, file);
                mediaPlayer.setDataSource(baseContext, voiceUri);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                        playingId = null;
                        onCompletionListener = null;
                    }
                });
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();

        /**
         * This listener is to stop the voice play animation currently, considered the following 3 conditions:
         *
         * 1.A new voice item is clicked to play, to stop the previous playing voice item animation.
         * 2.The voice is play complete, to stop it's voice play animation.
         * 3.Press the voice record button will stop the voice play and must stop voice play animation.
         *
         */
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }

    public void release() {
        onCompletionListener = null;
    }

    private FastChatRowVoicePlayer(Context cxt) {
        baseContext = cxt.getApplicationContext();
        audioManager = (AudioManager) baseContext.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
    }

    private void setSpeaker() {
        boolean speakerOn = SettingsManager.getInstance().getSettingMsgSpeaker();
        if (speakerOn) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
    }
}
