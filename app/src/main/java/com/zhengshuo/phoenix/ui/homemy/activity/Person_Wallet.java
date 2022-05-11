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
import com.zhengshuo.phoenix.ui.dialog.GiftExchangeDialog;
import com.zhengshuo.phoenix.ui.dialog.ZanDialog;
import com.zhengshuo.phoenix.widget.FlowLayout;

/**
 * 我的钱包
 */
public class Person_Wallet extends BaseBindingActivity<ActivityPersonwalletBinding> implements View.OnClickListener {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Person_Wallet.class);
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
        giftTypeList(getBinding().vipTypeFlow, mActivity);

        getBinding().moneyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person_Assets.startActivity(mActivity);
            }
        });
//        getBinding().tvBtn.setTextAfter("秒后重新排队").setTextBefore("点击开始排队").setLenght(3*1000);
    }

//    @Override
//    protected void initEvent() {
//        getBinding().moneyTV.setOnClickListener(this);
//    }

    public void giftTypeList(FlowLayout label_Flow, Context mContext) {
//        String[] vipData = new String[]{"", "", "", "", "", "", "", ""};
        int[] vipImgData = new int[]{R.mipmap.miegui, R.mipmap.huaping, R.mipmap.qiqiu, R.mipmap.dangao, R.mipmap.shengri, R.mipmap.gougou, R.mipmap.liwuhe, R.mipmap.jeizhi};
        if (label_Flow != null) {
            label_Flow.removeAllViews();
        }
        for (int i = 0; i < vipImgData.length; i++) {
            View contentView = View.inflate(mContext, R.layout.gift_type_popup, null);
            ConstraintLayout gift_Con = contentView.findViewById(R.id.gift_Con);
            ImageView gift_Img = contentView.findViewById(R.id.gift_Img);
            gift_Img.setImageResource(vipImgData[i]);
            TextView giftType_Txt = contentView.findViewById(R.id.giftType_Txt);
//            vipType_Txt.setText(vipData[i]);
            gift_Con.setTag(i);
            label_Flow.addView(contentView);
            gift_Con.setOnClickListener(v -> {
                int tag = (int) v.getTag();
                showGiftDialog();
            });
        }
    }

    protected void showGiftDialog() {//1 点击确定会返回， 0 只是弹窗消失
        GiftExchangeDialog mDialog = new GiftExchangeDialog(mActivity, "", "林允儿…共获得1.3w个赞", "确定", () -> {

        });
        mDialog.show();
    }


    private void setSe(TextView login_txt) {
        String str = "《充值/提现须知》《收益兑换说明》";
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
        }, start, start + 9, 0);
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
        }, end, end + 8, 0);
        login_txt.setMovementMethod(LinkMovementMethod.getInstance());
        login_txt.setText(ssb, TextView.BufferType.SPANNABLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moneyTV:
                Person_Assets.startActivity(mActivity);
                break;
        }
    }
}