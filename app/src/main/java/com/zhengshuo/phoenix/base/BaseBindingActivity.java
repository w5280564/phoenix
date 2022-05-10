package com.zhengshuo.phoenix.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.SimpleImmersionFragment;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.MainActivity;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.widget.CustomTitleBar;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: Activity 使用viewBinding基类
 * @CreateDate: 2022/5/4
 */
public abstract class BaseBindingActivity<VB extends ViewBinding> extends FragmentActivity implements CustomTitleBar.TitleBarClickListener {
    protected BaseApplication mApplication;
    protected Context mContext;
    protected FragmentActivity mActivity;
    protected CustomTitleBar titleBar;
    protected String title;
    private VB binding;

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


    public VB getBinding() {
        return binding;
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


    @Override
    protected void onResume() {
        super.onResume();
        if (titleBar == null) {
            View view = findViewById(R.id.title_bar);
            if (view != null) {
                titleBar = (CustomTitleBar) view;
                titleBar.setTitleBarClickListener(this);
                if (!TextUtils.isEmpty(title)) {
                    titleBar.setTitle(title);
                }
            }
        }
    }


    @Override
    public void onRightClick() {

    }

    /**
     * 页面回退
     * 栈中已经没有存在的页面时跳转到首页
     */
    protected void back() {
        int size = mApplication.getLifecycleCallbacks().count();
        if (size <= 1) {
            skipAnotherActivity(MainActivity.class);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 必须调用该方法，防止内存泄漏
        ImmersionBar.with(this).destroy();
        ToastUtil.cancel();
    }


    /**
     * 功能描述:简单地Activity的跳转(不携带任何数据)
     */
    protected void skipAnotherActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(mActivity, targetActivity);
        startActivity(intent);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }


    @Override
    public void onBackPressed() {
        hideKeyboard();
        back();
    }

    @Override
    public void onLeftClick() {
        hideKeyboard();
        back();
    }


    /**
     * hide keyboard
     */
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    protected List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private int cuurent = 0x001;

    protected void addFragment(Fragment fragment) {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        mFragmentList.add(fragment);
    }


    protected void showFragment(int index, int fragmentId) {
        if (cuurent != 0x001 && getCurrentFrl() == mFragmentList.get(index)) {
            return;
        }
        FragmentManager manage = getSupportFragmentManager();
        FragmentTransaction transaction = manage.beginTransaction();
        Fragment frl = mFragmentList.get(index);
        if (frl.isAdded()) {
            frl.onResume();
        } else {
            transaction.add(fragmentId, frl);
        }

        for (int i = 0; i < mFragmentList.size(); i++) {
            Fragment fragment = mFragmentList.get(i);
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (index == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
        transaction.commitAllowingStateLoss();
        cuurent = index;
    }

    protected Fragment getCurrentFrl() {

        return mFragmentList.get(cuurent);
    }



}
