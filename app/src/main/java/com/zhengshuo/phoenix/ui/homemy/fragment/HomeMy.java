package com.zhengshuo.phoenix.ui.homemy.fragment;

import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.base.BaseBindingFragment;
import com.zhengshuo.phoenix.databinding.HomemyBinding;

public class HomeMy extends BaseBindingFragment<HomemyBinding> {


    @Override
    public void initImmersionBar() {
        setStatusBar();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this)
                .titleBar(getBinding().mToolbar)
                .statusBarDarkFont(false)
                .init();
    }

    @Override
    protected void initView(View mRootView) {
        super.initView(mRootView);


        getBinding().ivImg.setTag("overScroll"); //BaseApplication中添加了ViewTarget
        getBinding().appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
//            if (percent == 0) {
//                groupChange(1f, 1);
//            } else if (percent == 1) {
//                groupChange(1f, 2);
//            } else {
//                groupChange(percent, 0);
//            }
        });
    }

}
