package com.zhengshuo.phoenix.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseTabLayoutBindingFragment;
import com.zhengshuo.phoenix.base.behavior.AppBarLayoutOverScrollViewBehavior;
import com.zhengshuo.phoenix.databinding.HomemyBinding;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.MainActivity;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMyDynamic;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMyVideo;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMyZan;
import com.zhengshuo.phoenix.util.DisplayUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;

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

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        initMenuData();
        changeAppLayout();
        changeView();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        getBinding().settingBtn.setOnClickListener(new drawer_left_ImgClick());
    }

    private class drawer_left_ImgClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((MainActivity) requireActivity()).LeftDL();
        }
    }



    private void changeView() {
//        String url = "https://static.pictureknow.com/cb13b3b7222c419aa3cdc9130f9c4fbc.png";
        String url = "";
        loadImage(requireActivity(), getBinding().ivImg, url, R.mipmap.mycard_bg);

        getBinding().cardHeadInclude.editUserTv.setVisibility(View.VISIBLE);
        getBinding().ivImg.setTag("overScroll"); //BaseApplication中添加了ViewTarget
//        getBinding().ivImg.setBackgroundResource(R.mipmap.mycard_bg);
//        getBinding().ivImg.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.text_color_4f59ff));
//
//        getBinding().cardHeadInclude.tvName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
//        getBinding().cardHeadInclude.tvName.setText("呵呵哈哈哈");
    }

    public static void loadImage(Context context, ImageView iv, String url, @DrawableRes int errorImg) {
        ColorFilterTransformation colorFilterTransformation = new ColorFilterTransformation(ContextCompat.getColor(context, R.color.color_33));
        if (TextUtils.isEmpty(url)) {
//            Glide.with(context).load(R.mipmap.mycard_bg).transform(colorFilterTransformation).into(iv);
        } else {
            Glide.with(context).load(url).transform(colorFilterTransformation).error(errorImg).placeholder(errorImg).into(iv);
        }

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
                ImmersionBar.with(requireActivity()).titleBar(mToolbar).statusBarDarkFont(false).init();//状态栏浅色字体
                setTint(getBinding().settingBtn, R.mipmap.gengduo, R.color.white);
//                mViewPager.setNoScroll(false);
                getBinding().ivImg.setVisibility(View.VISIBLE);
                getBinding().toolbarHeadImg.setVisibility(View.GONE);
                getBinding().toolbarNameTv.setVisibility(View.GONE);
                break;
            case 2://完全关闭 显示黑色
                ImmersionBar.with(requireActivity()).titleBar(mToolbar).statusBarDarkFont(true).init();//状态栏深色字体
                setTint(getBinding().settingBtn, R.mipmap.gengduo, R.color.text_color_535353);
                getBinding().ivImg.setVisibility(View.INVISIBLE);
                getBinding().toolbarHeadImg.setVisibility(View.VISIBLE);
                getBinding().toolbarNameTv.setVisibility(View.VISIBLE);
//                mViewPager.setNoScroll(false);
                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
                    getBinding().ivImg.setVisibility(View.VISIBLE);
                    setTint(getBinding().settingBtn, R.mipmap.gengduo, R.color.text_color_535353);
                }
                //为什么禁止滑动？在介于开关状态之间，不允许滑动，开启可能会导致不好的体验
//                mViewPager.setNoScroll(true);
                break;
        }
    }

    private void setMargins(int top) {
        int i = DisplayUtil.dip2px(requireActivity(), top);
        CoordinatorLayout.LayoutParams itemParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(0, i, 0, 0);
        getBinding().botLin.setLayoutParams(itemParams);
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
        fragments.add(new HomeMyDynamic());
        fragments.add(new HomeMyZan());

        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        if (selectedTabPosition == -1) {
            selectedTabPosition = 0;
        }
        initChildViewPager(selectedTabPosition);
    }


}
