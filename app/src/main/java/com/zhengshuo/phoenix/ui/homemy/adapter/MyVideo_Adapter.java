package com.zhengshuo.phoenix.ui.homemy.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.MyVideoBean;

/**
 * ================================================
 *
 * @Description:我的-我的视频
 * <p>
 * ================================================
 */
public class MyVideo_Adapter extends BaseQuickAdapter<MyVideoBean, BaseViewHolder> {
    public MyVideo_Adapter() {
        super(R.layout.myvideo_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MyVideoBean item) {
//        helper.setText(R.id.title_Tv,item.getTitle());
//        helper.setText(R.id.content_Tv,item.getContent());
//        helper.setText(R.id.time_Tv,item.getCreateTime());
//
//        helper.addOnClickListener(R.id.draft_Fa);
//        helper.addOnClickListener(R.id.draft_Del);
    }

}
