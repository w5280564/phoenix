package com.zhengshuo.phoenix.ui.homemy.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityPersoneditBinding;

/**
 * 编辑资料
 */
public class Person_Edit extends BaseBindingActivity<ActivityPersoneditBinding> {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Person_Edit.class);
        context.startActivity(intent);
    }


    @Override
    protected void initView() {
        super.initView();
        getBinding().qrcodeMyView.getIMG().setVisibility(View.VISIBLE);
        getBinding().qrcodeMyView.getIMG().setBackgroundResource(R.mipmap.erweima);
    }

}