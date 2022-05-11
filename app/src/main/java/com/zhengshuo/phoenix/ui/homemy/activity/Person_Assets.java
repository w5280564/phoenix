package com.zhengshuo.phoenix.ui.homemy.activity;

import android.content.Context;
import android.content.Intent;

import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityPersonassetsBinding;

/**
 * 我的资产
 */
public class Person_Assets extends BaseBindingActivity<ActivityPersonassetsBinding> {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Person_Assets.class);
        context.startActivity(intent);
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.color_F7F4FD)     //状态栏颜色，不写默认透明色
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    protected void initView() {
    }




}