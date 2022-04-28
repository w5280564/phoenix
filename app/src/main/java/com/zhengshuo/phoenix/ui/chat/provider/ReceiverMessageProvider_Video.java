package com.zhengshuo.phoenix.ui.chat.provider;

import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.FastImageUtils;
import com.zhengshuo.phoenix.widget.RoundImageView;
import com.fastchat.sdk.model.HTMessage;

/**
 *接收视频消息
 */
public class ReceiverMessageProvider_Video extends BaseMessageProvider_Receiver {


    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_VIDEO_RECEIVED;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_receiver_video;
    }

    @Override
    public void convert(BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        RoundImageView iv_image_content = helper.getView(R.id.iv_image_content);
        showImageView(item,iv_image_content);
    }



    private void showImageView(final HTMessage htMessage, final RoundImageView iv_image_content) {
        FastImageUtils.showVideoThumb(mContext, iv_image_content, htMessage);
    }
}
