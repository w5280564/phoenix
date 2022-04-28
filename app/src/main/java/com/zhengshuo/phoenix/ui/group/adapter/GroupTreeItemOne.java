package com.zhengshuo.phoenix.ui.group.adapter;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baozi.treerecyclerview.base.ViewHolder;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.baozi.treerecyclerview.item.TreeItemGroup;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.MyGroupBigBean;

import java.util.List;

/**
 * Created by baozi on 2016/12/8.
 */
public class GroupTreeItemOne extends TreeItemGroup<MyGroupBigBean> {


    @Override
    public int getLayoutId() {
        return R.layout.itme_group_one;
    }

    @Override
    public boolean isCanExpand() {
        return true;
    }

    @Override
    protected List<TreeItem> initChild(MyGroupBigBean data) {
        List<TreeItem> items = ItemHelperFactory.createItems(data.getGroupList(), this);
        return items;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder) {
        if (isExpand()) {
            holder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            holder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
        holder.setText(R.id.tv_name, data.getListName());
        holder.setText(R.id.tv_content, data.getCount()+"");
    }

    @Override
    public void onClick(ViewHolder viewHolder) {
        super.onClick(viewHolder);
        if (isExpand()) {
            viewHolder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            viewHolder.setImageResource(R.id.iv_right, R.drawable.ic_keyboard_arrow_right_black_24dp);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, RecyclerView.LayoutParams layoutParams, int position) {
        super.getItemOffsets(outRect, layoutParams, position);
        outRect.bottom = 1;
    }
}
