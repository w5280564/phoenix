package com.zhengshuo.phoenix.ui.homemy.activity;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityPersonwalletBinding;
import com.zhengshuo.phoenix.databinding.ActivityPersonwalletdescBinding;
import com.zhengshuo.phoenix.ui.dialog.GiftExchangeDialog;
import com.zhengshuo.phoenix.widget.FlowLayout;

/**
 * 我的钱包-开通说明
 */
public class Person_WalletDesc extends BaseBindingActivity<ActivityPersonwalletdescBinding> implements View.OnClickListener {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Person_WalletDesc.class);
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
        setSe(getBinding().tvAgreement);

        getBinding().tvBtn.setColorBefore(R.drawable.gradualbtn_shape_grey);
        getBinding().tvBtn.setColorAfter(R.drawable.gradualbtn_shape);
        getBinding().tvBtn.setTextBefore("同意开通").setTextAfter("同意开通").setLenght(3*1000);
    }

    @Override
    protected void initEvent() {
        getBinding().tvBtn.setOnClickListener(this);
    }



    private void setSe(TextView login_txt) {
        String str = "《用户服务协议》 《隐私政策》";
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);
        final int start = str.indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                jumpToWebView("用户协议", H5Url.H5UserPolicy);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_color_4f59ff));
                ds.setUnderlineText(false);
            }
        }, start, start + 8, 0);
        int end = str.lastIndexOf("《");
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                jumpToWebView("隐私政策", H5Url.H5PrivatePolicy);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_color_4f59ff));
                ds.setUnderlineText(false);
            }
        }, end, end + 6, 0);
        login_txt.setMovementMethod(LinkMovementMethod.getInstance());
        login_txt.setText(ssb, TextView.BufferType.SPANNABLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBtn:
                Person_Wallet.startActivity(mActivity);
                break;
        }
    }
}