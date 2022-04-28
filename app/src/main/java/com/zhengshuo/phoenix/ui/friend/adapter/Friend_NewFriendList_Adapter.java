package com.zhengshuo.phoenix.ui.friend.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.FriendApplyBean;
import com.zhengshuo.phoenix.util.DateUtils;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.RoundImageView;

/**
 * 好友申请列表
 */
public class Friend_NewFriendList_Adapter extends BaseQuickAdapter<FriendApplyBean, BaseViewHolder> {

    public Friend_NewFriendList_Adapter() {
        super(R.layout.friend_newfriendlist_adapter);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, FriendApplyBean item) {
        RoundImageView head_Simple = helper.getView(R.id.head_Simple);
        TextView name_Txt = helper.getView(R.id.name_Txt);
        TextView friend_Txt = helper.getView(R.id.friend_Txt);
        TextView apply_Txt = helper.getView(R.id.apply_Txt);
        TextView add_Btn = helper.getView(R.id.add_Btn);
        TextView refuse_Btn = helper.getView(R.id.refuse_Btn);
        TextView result_Txt = helper.getView(R.id.result_Txt);
        TextView time_Txt = helper.getView(R.id.time_Txt);

        helper.addOnClickListener(R.id.add_Btn);
        helper.addOnClickListener(R.id.refuse_Btn);

        ImgLoader.getInstance().displayCrop(mContext, head_Simple, item.getHeadImg(), R.mipmap.error_image_placeholder);


        name_Txt.setText(item.getNickname());
        friend_Txt.setText(StringUtil.isBlank(item.getRemark()) ? "请求加为好友" : item.getRemark());


        refuse_Btn.setVisibility(View.INVISIBLE);
        add_Btn.setVisibility(View.INVISIBLE);
        time_Txt.setVisibility(View.VISIBLE);
        time_Txt.setText(DateUtils.dateToString(item.getUpdateTime(), DateUtils.PATTERN_STANDARD16H));
        //0申请中1已通过2被拒绝3已过期
        if (item.getApplyStatus() == 0) {
            refuse_Btn.setVisibility(View.VISIBLE);
            add_Btn.setVisibility(View.VISIBLE);
            time_Txt.setVisibility(View.INVISIBLE);
        } else if (item.getApplyStatus() == 1) {
            result_Txt.setVisibility(View.VISIBLE);
            result_Txt.setText("已通过");
        } else if (item.getApplyStatus() == 2) {
            result_Txt.setVisibility(View.VISIBLE);
            result_Txt.setText("已拒绝");
        } else if (item.getApplyStatus() == 3) {
            result_Txt.setVisibility(View.VISIBLE);
            result_Txt.setText("已过期");
        }
    }



}
