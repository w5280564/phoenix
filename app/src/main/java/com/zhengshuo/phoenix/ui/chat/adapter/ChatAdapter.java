package com.zhengshuo.phoenix.ui.chat.adapter;

import android.util.SparseArray;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.zhengshuo.phoenix.ui.chat.provider.NoticeMessageProvider;
import com.zhengshuo.phoenix.ui.chat.provider.ReceiverMessageProvider_Image;
import com.zhengshuo.phoenix.ui.chat.provider.ReceiverMessageProvider_Video;
import com.zhengshuo.phoenix.ui.chat.provider.ReceiverMessageProvider_Voice;
import com.zhengshuo.phoenix.ui.chat.provider.SenderMessageProvider_Image;
import com.zhengshuo.phoenix.ui.chat.provider.SenderMessageProvider_Video;
import com.zhengshuo.phoenix.ui.chat.provider.SenderMessageProvider_Voice;
import com.fastchat.sdk.model.HTMessage;
import java.util.List;
import com.zhengshuo.phoenix.ui.chat.provider.ReceiverMessageProvider_Text;
import com.zhengshuo.phoenix.ui.chat.provider.SenderMessageProvider_Text;

/**
 * 聊天列表适配器
 */
public class ChatAdapter extends MultipleItemRvAdapter<HTMessage, BaseViewHolder> {

    public static final int MESSAGE_TEXT_RECEIVED = 1;
    public static final int MESSAGE_TEXT_SEND = 2;
    public static final int MESSAGE_IMAGE_RECEIVED = 3;
    public static final int MESSAGE_IMAGE_SEND = 4;
    public static final int MESSAGE_VOICE_RECEIVED = 5;
    public static final int MESSAGE_VOICE_SEND = 6;
    public static final int MESSAGE_FILE_RECEIVED = 7;
    public static final int MESSAGE_FILE_SEND = 8;
    public static final int MESSAGE_VIDEO_RECEIVED = 9;
    public static final int MESSAGE_VIDEO_SEND = 10;
    public static final int MESSAGE_LOCATION_RECEIVED = 11;
    public static final int MESSAGE_LOCATION_SEND = 12;

    public static final int MESSAGE_NOTICE = 17;
    public static final int MESSAGE_CARD_RECEIVED = 18;
    public static final int MESSAGE_CARD_SEND = 19;


    public ChatAdapter(List<HTMessage> data) {
        super(data);
        finishInitialize();
    }

    @Override
    protected int getViewType(HTMessage entity) {
        return getItemViewType(entity);
    }



    private int getItemViewType(HTMessage htMessage) {
        HTMessage.Type type = htMessage.getType();
        int action = htMessage.getIntAttribute("action", 0);
        if ((action < 2006 && action > 1999) || action == 6001) {
            //群相关通知消息||撤回消息
            return MESSAGE_NOTICE;
        } else if (action == 10007) {
            //名片消息
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_CARD_RECEIVED : MESSAGE_CARD_SEND;
        } else if (type == HTMessage.Type.IMAGE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_IMAGE_RECEIVED : MESSAGE_IMAGE_SEND;
        } else if (type == HTMessage.Type.VOICE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VOICE_RECEIVED : MESSAGE_VOICE_SEND;
        } else if (type == HTMessage.Type.VIDEO) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VIDEO_RECEIVED : MESSAGE_VIDEO_SEND;
        } else if (type == HTMessage.Type.FILE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_FILE_RECEIVED : MESSAGE_FILE_SEND;
        } else if (type == HTMessage.Type.LOCATION) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_LOCATION_RECEIVED : MESSAGE_LOCATION_SEND;
        } else{
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_TEXT_RECEIVED : MESSAGE_TEXT_SEND;
        }
    }


    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new SenderMessageProvider_Text());
        mProviderDelegate.registerProvider(new SenderMessageProvider_Image());
        mProviderDelegate.registerProvider(new SenderMessageProvider_Video());
        mProviderDelegate.registerProvider(new SenderMessageProvider_Voice());

        mProviderDelegate.registerProvider(new ReceiverMessageProvider_Text());
        mProviderDelegate.registerProvider(new ReceiverMessageProvider_Image());
        mProviderDelegate.registerProvider(new ReceiverMessageProvider_Video());
        mProviderDelegate.registerProvider(new ReceiverMessageProvider_Voice());

        mProviderDelegate.registerProvider(new NoticeMessageProvider());//通知类消息（群通知，消息撤回等）
    }


    public BaseItemProvider getVoiceProvider (int message_type){
        SparseArray<BaseItemProvider> mBaseItemProviders = mProviderDelegate.getItemProviders();
        for (int i = 0; i < mBaseItemProviders.size(); i++) {
            int key = mBaseItemProviders.keyAt(i);
            if (key==message_type) {
                return mBaseItemProviders.get(key);
            }
        }
        return null;
    }


}
