package com.zhengshuo.phoenix.widget.NineGrid;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.widget.glide.GlideUtils;
import com.zhengshuo.phoenix.util.DisplayUtil;

public class NineGridAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public NineGridAdapter() {
        super(R.layout.item_nine_grid);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView image = helper.getView(R.id.image);
        int radiusSize = DisplayUtil.dip2px(mContext, 4);
        GlideUtils.loadRoundImage(mContext,image,item,radiusSize);
    }

}
