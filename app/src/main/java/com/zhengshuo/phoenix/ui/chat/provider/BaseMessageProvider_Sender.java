package com.zhengshuo.phoenix.ui.chat.provider;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.fastchat.sdk.model.HTMessage;

public class BaseMessageProvider_Sender extends BaseItemProvider <HTMessage, BaseViewHolder>{

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
        ImageView msg_status = helper.getView(R.id.msg_status);
        ProgressBar progress_bar = helper.getView(R.id.progress_bar);
        TextView tv_time = helper.getView(R.id.tv_time);
        helper.addOnClickListener(R.id.msg_status);
        helper.addOnClickListener(R.id.bubble);
        helper.addOnLongClickListener(R.id.bubble);


        ImgLoader.getInstance().displayCrop(mContext, iv_header, UserManager.get().getMyAvatar(), R.mipmap.error_image_placeholder, R.mipmap.error_image_placeholder);

        HTMessage.Status status = item.getStatus();
        if (status == HTMessage.Status.CREATE) {
            //通知消息没有progressBar
            progress_bar.setVisibility(View.VISIBLE);
        } else {
            progress_bar.setVisibility(View.GONE);
        }

        if (status == HTMessage.Status.FAIL) {
            //通知消息没有ivMsgStatus
            msg_status.setVisibility(View.VISIBLE);
        } else {
            msg_status.setVisibility(View.GONE);
        }

        if (position == 0) {
            tv_time.setText(StringUtil.getStringValue(DateUtils.dateToString(item.getTime(),DateUtils.PATTERN_STANDARD20H)));
            tv_time.setVisibility(View.VISIBLE);
        } else {
            // 两条消息大于1分钟
            long duration = item.getTime() - getItem(position - 1).getTime();
            if (duration >= 60000) {
                tv_time.setText(StringUtil.getStringValue(DateUtils.dateToString(item.getTime(),DateUtils.PATTERN_STANDARD20H)));
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
