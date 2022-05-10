package com.zhengshuo.phoenix.ui.homemy.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.zhengshuo.phoenix.base.BaseRecyclerSplitBindingFragment;
import com.zhengshuo.phoenix.databinding.HomemyvideoBinding;
import com.zhengshuo.phoenix.model.MyVideoBean;
import com.zhengshuo.phoenix.ui.homemy.adapter.MyVideo_Adapter;

import java.util.ArrayList;

public class HomeMyVideo extends BaseRecyclerSplitBindingFragment<HomemyvideoBinding> {

    @Override
    protected void initView(View mRootView) {
        super.initView(mRootView);
        mRecyclerView = getBinding().cardRecycler;
        initRecyclerView();
        initSwipeRefreshLayoutAndAdapter("暂无数据", 0, false);
    }


    @Override
    protected void onRefreshData() {

    }

    @Override
    protected void onLoadMoreData() {

    }


    public void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyVideo_Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {

        });
        empty();
    }

    private void empty() {
        MyVideoBean myVideoBean = new MyVideoBean();
        ArrayList<MyVideoBean> myVideoBeans = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            myVideoBeans.add(myVideoBean);
        }
        mAdapter.setNewData(myVideoBeans);
    }
}
