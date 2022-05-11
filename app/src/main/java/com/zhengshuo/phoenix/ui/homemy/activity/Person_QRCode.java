package com.zhengshuo.phoenix.ui.homemy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityPersonqrcodeBinding;

public class Person_QRCode extends BaseBindingActivity<ActivityPersonqrcodeBinding> {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Person_QRCode.class);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
//        super.setStatusBar();
        setBar();
    }



    /**
     * 设置状态栏
     */
    private void setBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    protected void initView() {
        super.initView();
        TextView leftBtnView = getBinding().titleBar.getLeftBtnView();
        setBg(leftBtnView, R.mipmap.fanhui, R.color.white);
    }


    /**
     * 修改top图片颜色
     *
     * @param textView
     * @param resourceId
     * @param color
     */
    private void setBg(TextView textView, int resourceId, int color) {
        Drawable drawable = getResources().getDrawable(resourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, color));
        textView.setBackground(drawable);
    }
}