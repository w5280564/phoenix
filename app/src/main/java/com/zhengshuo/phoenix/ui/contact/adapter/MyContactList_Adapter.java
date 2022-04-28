package com.zhengshuo.phoenix.ui.contact.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.RoundImageView;

public class MyContactList_Adapter extends BaseQuickAdapter<FriendBean, BaseViewHolder> {


    public MyContactList_Adapter() {
        super(R.layout.item_contact_list);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FriendBean item) {
        TextView headerView = helper.getView(R.id.header);
        RoundImageView avatar = helper.getView(R.id.avatar);
        TextView mName = helper.getView(R.id.name);
        TextView tv_id = helper.getView(R.id.tv_id);

        mName.setText(StringUtil.getStringValue(item.getRemarkName()));
        tv_id.setText(String.format("IM号：%s",item.getShowId()));
        ImgLoader.getInstance().displayCrop(mContext, avatar, item.getHeadImg(), R.mipmap.error_image_placeholder);

        String header = item.getInitialLetter();

        int position = helper.getAbsoluteAdapterPosition();
        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                headerView.setVisibility(View.GONE);
            } else {
                headerView.setVisibility(View.VISIBLE);
                headerView.setText(header);
            }
        } else {
            headerView.setVisibility(View.GONE);
        }
    }


}
