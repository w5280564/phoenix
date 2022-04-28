package com.zhengshuo.phoenix.ui.chat.weight;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.common.manager.ChatFileManager;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.ui.chat.provider.ReceiverMessageProvider_Voice;
import com.zhengshuo.phoenix.ui.chat.provider.SenderMessageProvider_Voice;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.util.StringUtil;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageVoiceBody;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import java.io.File;
import java.util.ArrayList;
import okhttp3.Call;


public class VoicePlayClickListener implements View.OnClickListener {
    private static final String TAG = "VoicePlayClickListener";
    private AnimationDrawable voiceAnimation = null;
    private HTMessage message;
    private ImageView voiceIconView;
    private ImageView iv_read_status;
    private ProgressBar progress_bar;
    private ChatAdapter adapter;
    private FastChatRowVoicePlayer voicePlayer;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;
    private ArrayList<HTMessage> messageArrayList;
    private int position = 0;
    private Handler handler=new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    progress_bar.setVisibility(View.GONE);
                    String localPath= (String) msg.obj;
                    HTMessageVoiceBody voiceBody = (HTMessageVoiceBody) message.getBody();
                    voiceBody.setLocalPath(localPath);
                    message.setBody(voiceBody);
                    playVoice(localPath);
                    break;
                case 1001:
                    progress_bar.setVisibility(View.VISIBLE);
                    break;
                case 1002:
                    progress_bar.setVisibility(View.GONE);
                    break;
            }
        }
    }  ;


    public VoicePlayClickListener(HTMessage message, ImageView voiceIconView, ImageView iv_read_status, ProgressBar progress_bar, ChatAdapter adapter, Activity context) {
        this.message = message;
        this.voiceIconView = voiceIconView;
        this.iv_read_status = iv_read_status;
        this.progress_bar = progress_bar;
        this.adapter = adapter;
        voicePlayer = FastChatRowVoicePlayer.getInstance(context);
    }


    /**
     * 停止播放
     */
    public void stopPlayVoice() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.yuyin3_receiver);
        } else {
            voiceIconView.setImageResource(R.drawable.yuyin3_send);
        }
        if (voicePlayer != null) {
            LogUtil.e("HTMessageVoiceBody", "mediaPlayer.stop()");
            voicePlayer.stop();
            voicePlayer.release();
        }
        isPlaying = false;
        playMsgId = null;
    }


    /**
     * 播放本地声音
     * @param filePath
     */
    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        voicePlayer.play(message, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                if (message.getDirect() == HTMessage.Direct.RECEIVE) {
                    ReceiverMessageProvider_Voice mReceiverMessageProvider_Voice = (ReceiverMessageProvider_Voice) adapter.getVoiceProvider(ChatAdapter.MESSAGE_VOICE_RECEIVED);
                    if (mReceiverMessageProvider_Voice !=null) {
                        mReceiverMessageProvider_Voice.stopVoicePlayAnimation(voiceIconView);
                    }
                }else{
                    SenderMessageProvider_Voice mSenderMessageProvider_Voice = (SenderMessageProvider_Voice) adapter.getVoiceProvider(ChatAdapter.MESSAGE_VOICE_SEND);
                    if (mSenderMessageProvider_Voice !=null) {
                        mSenderMessageProvider_Voice.stopVoicePlayAnimation(voiceIconView);
                    }
                }
            }
        });

        playMsgId = message.getMsgId();
        isPlaying = true;
        currentPlayListener = this;

        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            ReceiverMessageProvider_Voice mReceiverMessageProvider_Voice = (ReceiverMessageProvider_Voice) adapter.getVoiceProvider(ChatAdapter.MESSAGE_VOICE_RECEIVED);
            if (mReceiverMessageProvider_Voice !=null) {
                mReceiverMessageProvider_Voice.startVoicePlayAnimation(voiceIconView,iv_read_status);
            }
            if (message.getStatus() != HTMessage.Status.SUCCESS && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {// 如果是接收的消息
                // 隐藏自己未播放这条语音消息的标志
                iv_read_status.setVisibility(View.INVISIBLE);
                HTClient.getInstance().messageManager().updateSuccess(message);
            }
        }else{
            SenderMessageProvider_Voice mSenderMessageProvider_Voice = (SenderMessageProvider_Voice) adapter.getVoiceProvider(ChatAdapter.MESSAGE_VOICE_SEND);
            if (mSenderMessageProvider_Voice !=null) {
                mSenderMessageProvider_Voice.startVoicePlayAnimation(voiceIconView,iv_read_status);
            }
        }
    }




    @Override
    public void onClick(View v) {
        LogUtil.e("HTMessageVoiceBody", "点击播放playMsgId>>>"+playMsgId);
        LogUtil.e("HTMessageVoiceBody", "点击播放message.getMsgId()>>>"+message.getMsgId());
        LogUtil.e("HTMessageVoiceBody", "点击播放isPlaying>>>"+isPlaying);
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                LogUtil.e("HTMessageVoiceBody", "stopPlayVoice");
                stopPlayVoice();
                return;
            }
        }
        String filePath= ChatFileManager.get().getLocalPath(message.getMsgId(), message.getType());
        if(filePath == null) {
            HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) message.getBody();
            String remotePath = htMessageVoiceBody.getRemotePath();
            String fileName = htMessageVoiceBody.getFileName();
            if (StringUtil.isBlank(fileName)) {
                fileName = System.currentTimeMillis() + ".m4a";
            }
            downLoadVoiceFileFromServer(remotePath, fileName);
        }else {
            HTMessageVoiceBody voiceBody = (HTMessageVoiceBody)message.getBody();
            voiceBody.setLocalPath(filePath);
            message.setBody(voiceBody);
            File file = new File(filePath);
            if (file.exists() && file.isFile())
                playVoice(filePath);
            else
                Log.e(TAG, "file not exist");

        }
    }


    /**
     * 下载声音
     * @param remotePath
     * @param fileName
     */
    private void downLoadVoiceFileFromServer(String  remotePath,String fileName ) {
        String localPath = BaseApplication.getInstance().getVoicePath();
        OkHttpUtils.get()
                .url(remotePath)
                .build()
                .execute(new FileCallBack(localPath, fileName) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.e("HTMessageVoiceBody", "onError :" + e.getMessage());
                        Message message=handler.obtainMessage();
                        message.what=1002;
                        message.sendToTarget();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        Message message=handler.obtainMessage();
                        message.what=1001;
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(File downloadFile, int id) {
                        ChatFileManager.get().setLocalPath(message.getMsgId(),downloadFile.getAbsolutePath(),message.getType());
                        Message message=handler.obtainMessage();
                        message.what=1000;
                        message.obj=downloadFile.getAbsolutePath();
                        message.sendToTarget();
                    }
                });
    }



    /**
     * 下一个声音
     * @param messageArrayList
     */
    private void nextSound(ArrayList<HTMessage> messageArrayList) {
        if (position < messageArrayList.size() - 1) {
            position = position + 1;
            playVoice();
        } else {
//            messageArrayList.clear();
            position = 0;
        }
    }

    public void playVoice() {
        message = messageArrayList.get(position);
        HTMessageVoiceBody body = (HTMessageVoiceBody) message.getBody();
        playMsgId = message.getMsgId();

        voicePlayer.play(message, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 隐藏自己未播放这条语音消息的标志
                if (iv_read_status != null) {
                    iv_read_status.setVisibility(View.INVISIBLE);
                }
                HTClient.getInstance().messageManager().updateSuccess(message);
                stopPlayVoice(); // stop animation
                nextSound(messageArrayList);
            }
        });
        isPlaying = true;
        currentPlayListener = this;
        // 如果是接收的消息
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            if (message.getStatus() != HTMessage.Status.SUCCESS && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
                // 隐藏自己未播放这条语音消息的标志
                iv_read_status.setVisibility(View.INVISIBLE);
                HTClient.getInstance().messageManager().updateSuccess(message);
            }
        }
    }
}
