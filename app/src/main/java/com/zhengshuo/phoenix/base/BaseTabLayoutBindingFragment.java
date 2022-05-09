package com.zhengshuo.phoenix.base;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.ViewPager;

import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.SimpleImmersionFragment;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.baseview.NormalFragmentAdapter;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

/**
 * tablayout的基类
 */
public class BaseTabLayoutBindingFragment<VB extends ViewBinding> extends SimpleImmersionFragment {
    protected List<Fragment> fragments = new ArrayList<>();
    protected List<AppMenuBean> menuList = new ArrayList<>();
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    private int selectSize = 18;
    private int normalSize = 15;
    private VB binding;

    protected BaseApplication mApplication;
    protected Context mContext;
    protected BaseBindingActivity mActivity;
    private Unbinder unbinder;
    private LoadingDialog mLoadingDialog;
    private Handler handler = new Handler();

    public VB getBinding() {
        return binding;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewBindingUtil.inflateWithGeneric(this, inflater, container, false);
        mApplication = BaseApplication.getInstance();
        mContext = mApplication.getContext();
        mActivity = (BaseBindingActivity) getActivity();
        initLocalData();
        //view与数据绑定
        initView(binding.getRoot());
        //设置监听
        initEvent();
        InitViewModel();
        return binding.getRoot();
    }

    @Override
    public void initImmersionBar() {

    }

    /**
     * 初始化本地数据
     */
    protected void initLocalData() {

    }


//    @Override
//    protected void initView(View mRootView) {
//        binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater());
//    }

    /**
     * 初始化view
     */
    protected void initView(View mRootView) {

    }


    /**
     * 设置ViewModel
     */
    protected void InitViewModel() {

    }


    public void setTabTextSize(int selectSize, int normalSize) {
        this.selectSize = selectSize;
        this.normalSize = normalSize;
    }

//    @Override
//    protected int getLayoutId() {
//        return 0;
//    }


    protected void initEvent() {
        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new MyOnTabSelectedListener());
        }
    }


    public class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
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


    protected void initViewPager(int position) {
        NormalFragmentAdapter mFragmentAdapter = new NormalFragmentAdapter(mActivity.getSupportFragmentManager(), fragments, menuList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(menuList.size());
        //将TabLayout和ViewPager关联起来。
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(position);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        View view = mTabLayout.getTabAt(position).getCustomView();
        if (null != view) {
            setTextViewStyle(view, 18, R.color.text_color_444444, Typeface.DEFAULT_BOLD, View.VISIBLE);
        }
        mViewPager.setCurrentItem(position);
    }

    /**
     * fragment中添加fragment 使用此方法
     *
     * @param position 页面下标
     */
    protected void initChildViewPager(int position) {
        NormalFragmentAdapter mFragmentAdapter = new NormalFragmentAdapter(getChildFragmentManager(), fragments, menuList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(menuList.size());
        //将TabLayout和ViewPager关联起来。
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(position);
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


    protected void setTextViewStyle(View view, int size, int color, Typeface textStyle, int visibility) {
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
    public TextView unread_msg_Tv;

    public View getTabView(int currentPosition) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        TextView tab_Name = view.findViewById(R.id.tab_item_textview);
        unread_msg_Tv = view.findViewById(R.id.unread_msg_Tv);
        tab_Name.setText(menuList.get(currentPosition).getName());
        return view;
    }

}
