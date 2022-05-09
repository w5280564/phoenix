package com.zhengshuo.phoenix.ui.home.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseTabLayoutBindingFragment;
import com.zhengshuo.phoenix.base.baseview.glide.GlideUtils;
import com.zhengshuo.phoenix.base.behavior.AppBarLayoutOverScrollViewBehavior;
import com.zhengshuo.phoenix.databinding.HomemyBinding;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMyVideo;

import java.util.ArrayList;

public class HomeMy extends BaseTabLayoutBindingFragment<HomemyBinding> {
    private Toolbar mToolbar;

    @Override
    public void initImmersionBar() {
        setStatusBar();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this)
                .titleBar(mToolbar)
                .statusBarDarkFont(false)
                .init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void initView(View mRootView) {
        super.initView(mRootView);

        mTabLayout = getBinding().cardTab;
        mViewPager = getBinding().cardViewPager;
        mToolbar = getBinding().mToolbar;
//        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        itemParams.setMargins(0, -30, 0, 0);
//        getBinding().botLin.setLayoutParams(itemParams);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        initMenuData();
        changeAppLayout();

        changeView();
    }

    private void changeView() {
        getBinding().ivImg.setTag("overScroll"); //BaseApplication中添加了ViewTarget
//        getBinding().ivImg.setBackgroundResource(R.mipmap.mycard_bg);
//        getBinding().ivImg.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.text_color_4f59ff));
        GlideUtils.loadImage(requireActivity(), getBinding().ivImg, null, R.mipmap.mycard_bg);
        getBinding().cardHeadInclude.tvName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
        getBinding().cardHeadInclude.tvName.setText("呵呵哈哈哈");
    }

    /**
     * 滑动显示 /下拉完成刷新
     */
    private void changeAppLayout() {
        getBinding().appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
            if (percent == 0) {
                groupChange(1f, 1);
            } else if (percent == 1) {
                groupChange(1f, 2);
            } else {
                groupChange(percent, 0);
            }
        });

        AppBarLayoutOverScrollViewBehavior myAppBarLayoutBehavoir = (AppBarLayoutOverScrollViewBehavior)
                ((CoordinatorLayout.LayoutParams) getBinding().appbarLayout.getLayoutParams()).getBehavior();
        myAppBarLayoutBehavoir.setOnProgressChangeListener(new AppBarLayoutOverScrollViewBehavior.onProgressChangeListener() {
            @Override
            public void onProgressChange(float progress, boolean isRelease) {
                if (progress == 0 && isRelease) {//进度条回到0 并且已释放 刷新一次数据
//                    getUserData(userID, true);
//                    initMenuData();
                }
            }
        });
    }

    private int lastState = 1;

    /**
     * @param alpha
     * @param state 0-正在变化 1展开 2 关闭
     */
    public void groupChange(float alpha, int state) {
        lastState = state;
        mToolbar.setAlpha(1);//一直需要展示的状态
        switch (state) {
            case 1://完全展开 显示白色
//                mToolbar.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.transparent));
                ImmersionBar.with(requireActivity()).titleBar(mToolbar).statusBarDarkFont(false).init();//状态栏浅色字体
//                setTint(share_Btn, R.mipmap.person_zhaunfa, R.color.white);
//                setTint(setting_Btn, R.mipmap.card_settings, R.color.white);
//                mViewPager.setNoScroll(false);

                getBinding().toolbarHeadImg.setVisibility(View.GONE);
                getBinding().toolbarNameTv.setVisibility(View.GONE);
                break;
            case 2://完全关闭 显示黑色
                ImmersionBar.with(requireActivity()).titleBar(mToolbar).statusBarDarkFont(true).init();//状态栏深色字体
//                setTint(share_Btn, R.mipmap.person_zhaunfa, R.color.text_color_6f6f6f);
//                setTint(setting_Btn, R.mipmap.card_settings, R.color.text_color_6f6f6f);
//                ConstraintLayout.LayoutParams itemParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//                itemParams.setMargins(0, 0, 0, 0);
//                getBinding().botLin.setLayoutParams(itemParams);

                getBinding().toolbarHeadImg.setVisibility(View.VISIBLE);
                getBinding().toolbarNameTv.setVisibility(View.VISIBLE);
//                mViewPager.setNoScroll(false);

                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
//                    setTint(share_Btn, R.mipmap.person_zhaunfa, R.color.text_color_6f6f6f);
                }
                //为什么禁止滑动？在介于开关状态之间，不允许滑动，开启可能会导致不好的体验
//                mViewPager.setNoScroll(true);
                break;
        }
    }


    /**
     * 修改按钮背景色
     *
     * @param button
     * @param drawable
     * @param color
     */
    public void setTint(Button button, int drawable, int color) {
        Drawable originalDrawable = ContextCompat.getDrawable(requireActivity(), drawable);
        Drawable wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate();
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireActivity(), color));
        button.setBackground(wrappedDrawable);
    }


    @SuppressLint("WrongConstant")
    private void initMenuData() {
        if (fragments != null) {
            fragments.clear();
        }
        if (menuList != null) {
            menuList.clear();
        }
        ArrayList<String> tabName = new ArrayList<>();
        tabName.add("我的视频");
        tabName.add("我的动态");
        tabName.add("我的点赞");
        for (int i = 0; i < tabName.size(); i++) {
            AppMenuBean bean = new AppMenuBean();
            bean.setName(tabName.get(i));
            menuList.add(bean);
        }
        fragments.add(new HomeMyVideo());
        fragments.add(new HomeMyVideo());
        fragments.add(new HomeMyVideo());
//        if (isMe()) {
//            fragments.add(MyCollect_Fragment.newInstance(userID));
//        }
//        fragments.add(MyArchives_Fragment.newInstance(userID));

        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        if (selectedTabPosition == -1) {
            selectedTabPosition = 0;
        }
        initChildViewPager(selectedTabPosition);
    }


}
