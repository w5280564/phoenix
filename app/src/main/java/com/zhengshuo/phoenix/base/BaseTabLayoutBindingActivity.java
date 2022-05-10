package com.zhengshuo.phoenix.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.ViewPager;

import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.widget.baseview.NormalFragmentAdapter;
import com.zhengshuo.phoenix.model.AppMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * tablayout的基类
 */
public class BaseTabLayoutBindingActivity<VB extends ViewBinding> extends FragmentActivity {
    protected List<Fragment> fragments = new ArrayList<>();
    protected List<AppMenuBean> menuList = new ArrayList<>();
    public TabLayout mTabLayout;
    public ViewPager mViewPager;
    private int selectSize = 16;
    private int normalSize = 14;
    protected BaseApplication mApplication;
    protected Context mContext;
    protected FragmentActivity mActivity;

//    @Override
//    protected int getLayoutId() {
//        return 0;
//    }

    public VB binding;

    public VB getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏
        binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater());
        setContentView(binding.getRoot());
        mApplication = BaseApplication.getInstance();
        mContext = mApplication.getContext();
        mActivity = this;
        //初始化本地数据
        initLocalData(getIntent());
        //view与数据绑定
        initView();
        initView(arg0);
        //初始化ViewModel
        initViewModel();
        //设置监听
        initEvent();
        //请求服务端接口数据
        setStatusBar();
    }

    /**
     * 初始化本地数据
     */
    protected void initLocalData(Intent mIntent) {

    }


    /**
     * 初始化view
     */
    protected void initView() {

    }


    /**
     * 初始化view
     */
    protected void initView(Bundle arg0) {

    }

    /**
     * 初始化ViewModel
     */
    protected void initViewModel() {

    }

    /**
     * 设置监听
     */
    protected void initEvent() {
        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new MyOnTabSelectedListener());
        }
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.white)     //状态栏颜色，不写默认透明色
                .statusBarDarkFont(true)
                .init();
    }


    public void setTabTextSize(int selectSize, int normalSize) {
        this.selectSize = selectSize;
        this.normalSize = normalSize;
    }


    public class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //在这里可以设置选中状态下  tab字体显示样式
//                mViewPager.setCurrentItem(tab.getPosition());
            View view = tab.getCustomView();
            if (null != view) {
                setTextViewStyle(view, selectSize, R.color.text_color_444444, Typeface.DEFAULT_BOLD, View.VISIBLE);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            if (null != view) {
                setTextViewStyle(view, normalSize, R.color.text_color_444444, Typeface.DEFAULT, View.INVISIBLE);
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }


    @SuppressLint("WrongConstant")
    protected void initViewPager(int position) {
//        mTabLayout.setTabMode(TabLayout.MODE_AUTO);
        NormalFragmentAdapter mFragmentAdapter = new NormalFragmentAdapter(getSupportFragmentManager(), fragments, menuList);
        if (mViewPager != null) {
            mFragmentAdapter.clear(mViewPager);
        }
        //给ViewPager设置适配器
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(menuList.size());
        //将TabLayout和ViewPager关联起来。
        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }

        View view = mTabLayout.getTabAt(position).getCustomView();
        if (null != view) {
            setTextViewStyle(view, 15, R.color.text_color_212121, Typeface.DEFAULT_BOLD, View.VISIBLE);
        }
        mViewPager.setCurrentItem(position);
    }


    private void setTextViewStyle(View view, int size, int color, Typeface textStyle, int visibility) {
        TextView mTextView = view.findViewById(R.id.tab_item_textview);
        View line = view.findViewById(R.id.line);
        mTextView.setTextSize(size);
        mTextView.setTextColor(ContextCompat.getColor(mContext, color));
        mTextView.setTypeface(textStyle);
        line.setVisibility(visibility);
    }


    /**
     * 自定义Tab的View
     *
     * @param currentPosition
     * @return
     */
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        textView.setTextSize(normalSize);
        textView.setText(menuList.get(currentPosition).getName());
        return view;
    }

}
