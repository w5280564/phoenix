package com.zhengshuo.phoenix.ui.group.adapter;


import androidx.annotation.NonNull;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.item.TreeItem;
import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.GroupListBean;
import com.zhengshuo.phoenix.ui.chat.activity.ChatActivity;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.widget.RoundImageView;

/**
 *
 */
public class GroupTreeItemTwo extends TreeItem<GroupListBean> {

    @Override
    public int getLayoutId() {
        return R.layout.item_group_two;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder) {
        holder.setText(R.id.tv_conten, data.getGroupName());
        RoundImageView iv_header = holder.getView(R.id.iv_header);
        ImgLoader.getInstance().displayCrop(iv_header.getContext(), iv_header, UserManager.get().getMyAvatar(), R.mipmap.error_image_placeholder, R.mipmap.error_image_placeholder);
    }


    @Override
    public int getSpanSize(int maxSpan) {
        return 0;
    }


    @Override
    public void onClick(ViewHolder viewHolder) {
        super.onClick(viewHolder);
        ChatActivity.actionStart(viewHolder.itemView.getContext(),data.getGroupImId(),MessageUtils.CHAT_GROUP);
    }
}
