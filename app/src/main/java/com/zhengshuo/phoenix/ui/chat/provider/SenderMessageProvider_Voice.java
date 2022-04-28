package com.zhengshuo.phoenix.ui.chat.provider;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.FastVoiceUtils;
import com.fastchat.sdk.model.HTMessage;

/**
 * 发送语音消息
 */
public class SenderMessageProvider_Voice extends BaseMessageProvider_Sender {
    private AnimationDrawable voiceAnimation = null;

    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_VOICE_SEND;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_sender_voice;
    }

    @Override
    public void convert(BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        ImageView iv_voice = helper.getView(R.id.iv_voice);
        TextView tv_length = helper.getView(R.id.tv_length);
        showVoiceView(item,iv_voice,tv_length);
    }

    public void startVoicePlayAnimation(ImageView iv_voice,ImageView iv_unread_voice) {
        iv_voice.setImageResource(R.drawable.ft_an_voice_send);
        voiceAnimation = (AnimationDrawable) iv_voice.getDrawable();
        voiceAnimation.start();
        iv_unread_voice.setVisibility(View.INVISIBLE);
    }

    public void stopVoicePlayAnimation(ImageView iv_voice) {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        iv_voice.setImageResource(R.drawable.yuyin3_send);
    }


    private void showVoiceView(HTMessage htMessage, ImageView iv_voice,TextView tv_length) {
        FastVoiceUtils.showVoiceView(iv_voice,tv_length, htMessage);
    }

}
