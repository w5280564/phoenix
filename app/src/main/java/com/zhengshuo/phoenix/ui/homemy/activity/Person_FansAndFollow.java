package com.zhengshuo.phoenix.ui.homemy.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.material.tabs.TabLayout;
import com.zhengshuo.phoenix.base.BaseTabLayoutBindingActivity;
import com.zhengshuo.phoenix.databinding.FanandfollowBinding;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMyFocus;

import java.util.ArrayList;

public class Person_FansAndFollow extends BaseTabLayoutBindingActivity<FanandfollowBinding> {

    private String tabName, UserID;

    public static void startActivity(Context context, String tabName, String UserID) {
        Intent intent = new Intent(context, Person_FansAndFollow.class);
        intent.putExtra("tabName", tabName);
        intent.putExtra("UserID", UserID);
        context.startActivity(intent);
    }

    @Override
    protected void initLocalData(Intent mIntent) {
        super.initLocalData(mIntent);
        tabName = mIntent.getStringExtra("tabName");
        UserID = mIntent.getStringExtra("UserID");
    }

    @Override
    protected void initView() {
        super.initView();
        mTabLayout = getBinding().cardTab;
        mViewPager = getBinding().cardViewPager;
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        initMenuData();
        int selectedTabPosition = 0;
        if (TextUtils.equals(this.tabName, "粉丝")) {
            selectedTabPosition = 1;
        }
        initViewPager(selectedTabPosition);
    }


    @Override
    protected void initEvent() {
        super.initEvent();
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
        tabName.add("关注");
        tabName.add("粉丝");
        for (int i = 0; i < tabName.size(); i++) {
            AppMenuBean bean = new AppMenuBean();
            bean.setName(tabName.get(i));
            menuList.add(bean);
        }
        fragments.add(new HomeMyFocus());
        fragments.add(new HomeMyFocus());

        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        if (selectedTabPosition == -1) {
            selectedTabPosition = 0;
        }
        initViewPager(selectedTabPosition);
    }


}