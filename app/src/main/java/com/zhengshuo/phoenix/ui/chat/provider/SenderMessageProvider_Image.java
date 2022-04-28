package com.zhengshuo.phoenix.ui.chat.provider;

import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.FastImageUtils;
import com.zhengshuo.phoenix.widget.RoundImageView;
import com.fastchat.sdk.model.HTMessage;

/**
 * 发送图片消息
 */
public class SenderMessageProvider_Image extends BaseMessageProvider_Sender {

    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_IMAGE_SEND;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_sender_image;
    }

    @Override
    public void convert(BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        RoundImageView iv_image_content = helper.getView(R.id.iv_image_content);
        showImageView(item,iv_image_content);
    }




    private void showImageView(HTMessage htMessage,RoundImageView iv_image_content) {
        FastImageUtils.showImage(mContext, iv_image_content, htMessage);
    }

}
