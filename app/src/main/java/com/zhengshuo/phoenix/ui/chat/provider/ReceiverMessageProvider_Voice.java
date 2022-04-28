package com.zhengshuo.phoenix.ui.chat.provider;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.manager.ChatFileManager;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.FastVoiceUtils;
import com.fastchat.sdk.model.HTMessage;

/**
 * 接收语音消息
 */
public class ReceiverMessageProvider_Voice extends BaseMessageProvider_Receiver {
    private AnimationDrawable voiceAnimation = null;

    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_VOICE_RECEIVED;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_receiver_voice;
    }

    @Override
    public void convert(@NonNull BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        ImageView iv_voice = helper.getView(R.id.iv_voice);
        TextView tv_length = helper.getView(R.id.tv_length);
        ImageView iv_unread_voice = helper.getView(R.id.iv_unread_voice);
        showVoiceView(item,iv_voice,tv_length,iv_unread_voice);
    }




    private void showVoiceView(HTMessage htMessage, ImageView iv_voice, TextView tv_length,ImageView iv_unread_voice) {
        FastVoiceUtils.showVoiceView(iv_voice, tv_length, htMessage);
        if (ChatFileManager.get().getLocalPath(htMessage.getMsgId(), htMessage.getType()) != null) {
            //如果本地有文件表示已经听过
            iv_unread_voice.setVisibility(View.GONE);
        } else {
            iv_unread_voice.setVisibility(View.VISIBLE);
        }
    }


    public void startVoicePlayAnimation(ImageView iv_voice,ImageView iv_unread_voice) {
        iv_voice.setImageResource(R.drawable.ft_an_voice_receive);
        voiceAnimation = (AnimationDrawable) iv_voice.getDrawable();
        voiceAnimation.start();
        // Hide the voice item not listened status view.
        iv_unread_voice.setVisibility(View.INVISIBLE);
    }

    public void stopVoicePlayAnimation(ImageView iv_voice) {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        iv_voice.setImageResource(R.drawable.yuyin3_receiver);
    }

}
