package com.zhengshuo.phoenix.ui.group.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.YRImageView;

import java.util.ArrayList;
import java.util.List;

public class GroupPickContactsAdapter extends BaseQuickAdapter<FriendBean, BaseViewHolder> {

    private List<String> selectedMembers_ids;
    private List<FriendBean> selectedMembers_objs;
    private OnSelectListener listener;


    public GroupPickContactsAdapter() {
        super(R.layout.layout_item_pick_contact_with_checkbox);
        selectedMembers_ids = new ArrayList<>();
        selectedMembers_objs = new ArrayList<>();
    }

    public List<String> getSelectedMembers() {
        return selectedMembers_ids;
    }

    public List<FriendBean> getFriendList() {
        return selectedMembers_objs;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FriendBean item) {
        TextView headerView = helper.getView(R.id.header);
        CheckBox checkbox = helper.getView(R.id.checkbox);
        YRImageView avatar = helper.getView(R.id.avatar);
        TextView mName = helper.getView(R.id.name);
        TextView tv_id = helper.getView(R.id.tv_id);
        ConstraintLayout cl_user = helper.getView(R.id.cl_user);
        String userID = item.getFriendUserId();

        mName.setText(StringUtil.getStringValue(item.getRemarkName()));
        tv_id.setText(String.format("ID：%s",StringUtil.getStringValue(item.getImId())));
        ImgLoader.getInstance().displayCrop(mContext, avatar, item.getHeadImg(), R.mipmap.error_image_placeholder);

        String header = item.getInitialLetter();

        int position = helper.getAdapterPosition();
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

        if ((!selectedMembers_ids.isEmpty() && selectedMembers_ids.contains(userID))) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }

        String finalUserID = userID;
        cl_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
                boolean checked = checkbox.isChecked();
                if (checked) {
                    if (!selectedMembers_ids.contains(finalUserID)) {
                        selectedMembers_ids.add(finalUserID);
                        selectedMembers_objs.add(item);
                    }
                } else {
                    if (selectedMembers_ids.contains(finalUserID)) {
                        selectedMembers_ids.remove(finalUserID);
                        selectedMembers_objs.remove(item);
                    }
                }
                if (listener != null) {
                    listener.onSelected(v, selectedMembers_ids);
                }
            }
        });
    }





    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelected(View v, List<String> selectedMembers);
    }

}
