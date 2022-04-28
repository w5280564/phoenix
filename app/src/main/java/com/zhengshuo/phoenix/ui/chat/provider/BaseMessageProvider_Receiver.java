package com.zhengshuo.phoenix.ui.chat.provider;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.util.DateUtils;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.RoundImageView;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.model.HTMessage;

public class BaseMessageProvider_Receiver extends BaseItemProvider <HTMessage, BaseViewHolder>{

    @Override
    public int viewType() {
        return 0;
    }

    @Override
    public int layout() {
        return 0;
    }

    @Override
    public void convert(@NonNull BaseViewHolder helper, HTMessage item, int position) {
        RoundImageView iv_header = helper.getView(R.id.iv_header);
        TextView tv_userName = helper.getView(R.id.tv_userName);
        TextView tv_time = helper.getView(R.id.tv_time);
        helper.addOnClickListener(R.id.bubble);
        helper.addOnLongClickListener(R.id.bubble);


        //先从缓存取，缓存没有再从消息体中取
        String userId = item.getUsername();
        if (item.getChatType() == ChatType.groupChat) {
            userId = item.getFrom();
            tv_userName.setVisibility(View.VISIBLE);
        }
        String nick = UserManager.get().getUserNick(userId);
        String avatar = UserManager.get().getUserAvatar(userId);
        if (TextUtils.isEmpty(nick)) {
            //此处只需要判断nick是否为空，因为avatar即使本地存了，也是为空的
            nick = item.getStringAttribute("nick");
            avatar = item.getStringAttribute("avatar");
        }
        tv_userName.setText(nick);
        ImgLoader.getInstance().displayCrop(mContext, iv_header, avatar, R.mipmap.error_image_placeholder, R.mipmap.error_image_placeholder);

        if (position == 0) {
            tv_time.setText(StringUtil.getStringValue(DateUtils.dateToString(item.getTime(), DateUtils.PATTERN_STANDARD20H)));
            tv_time.setVisibility(View.VISIBLE);
        } else {
            // 两条消息大于1分钟
            long duration = item.getTime() - getItem(position - 1).getTime();
            if (duration >= 60000) {
                tv_time.setText(StringUtil.getStringValue(DateUtils.dateToString(item.getTime(), DateUtils.PATTERN_STANDARD20H)));
                tv_time.setVisibility(View.VISIBLE);
            } else if (duration >= 0 && duration < 60000) {
                tv_time.setVisibility(View.GONE);
            } else if (duration < 0) {
                tv_time.setVisibility(View.GONE);
                //信息排序出现出现错误
                //  refreshData();
            } else {
                tv_time.setVisibility(View.GONE);
            }
        }
    }


    public HTMessage getItem(int position) {
        return mData.get(position);
    }
}
