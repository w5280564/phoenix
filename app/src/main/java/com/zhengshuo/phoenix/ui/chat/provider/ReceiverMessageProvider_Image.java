package com.zhengshuo.phoenix.ui.chat.provider;

import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.FastImageUtils;
import com.zhengshuo.phoenix.widget.RoundImageView;
import com.fastchat.sdk.model.HTMessage;

/**
 *接收图片消息
 */
public class ReceiverMessageProvider_Image extends BaseMessageProvider_Receiver {


    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_IMAGE_RECEIVED;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_receiver_image;
    }

    @Override
    public void convert(BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        RoundImageView iv_image_content = helper.getView(R.id.iv_image_content);
        showImageView(item,iv_image_content);
    }


    private void showImageView(final HTMessage htMessage, final RoundImageView iv_image_content) {
        ViewGroup.LayoutParams params = FastImageUtils.getImageShowSize(mContext, htMessage);
        ViewGroup.LayoutParams layoutParams = iv_image_content.getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        FastImageUtils.showImage(mContext, iv_image_content, htMessage);
    }
}
