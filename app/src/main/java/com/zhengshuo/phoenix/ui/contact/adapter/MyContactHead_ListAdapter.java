package com.zhengshuo.phoenix.ui.contact.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.ContactCustomBean;
import com.zhengshuo.phoenix.widget.YRImageView;

public class MyContactHead_ListAdapter extends BaseQuickAdapter<ContactCustomBean, BaseViewHolder> {


    public MyContactHead_ListAdapter() {
        super(R.layout.mycontact_head_item);
    }

    public void addItem(int id, int image, String name,boolean haveNoReadPoint) {
        ContactCustomBean bean = new ContactCustomBean();
        bean.setId(id);
        bean.setResourceId(image);
        bean.setName(name);
        bean.setHaveNoReadPoint(haveNoReadPoint);
        this.addData(bean);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ContactCustomBean item) {
        YRImageView mAvatar = helper.getView(R.id.avatar);
        TextView mName = helper.getView(R.id.name);
        TextView unread_msg = helper.getView(R.id.unread_msg);

        mName.setText(item.getName());
        if (item.getResourceId() != 0) {
            mAvatar.setImageResource(item.getResourceId());
        } else if (TextUtils.isEmpty(item.getImage())) {
            Glide.with(mContext).load(item.getImage()).into(mAvatar);
        }

        setShowPoint(unread_msg,item.isHaveNoReadPoint());
    }

    private void setShowPoint(View unread_msg, boolean isHaveNoRead) {
        if (isHaveNoRead) {
            if (unread_msg.getVisibility()!=View.VISIBLE) {
                unread_msg.setVisibility(View.VISIBLE);
            }
        }else{
            if (unread_msg.getVisibility()!=View.GONE) {
                unread_msg.setVisibility(View.GONE);
            }
        }
    }
}

