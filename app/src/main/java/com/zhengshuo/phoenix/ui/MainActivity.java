package com.zhengshuo.phoenix.ui;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.tabs.TabLayout;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityMainBinding;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.home.fragment.Home;
import com.zhengshuo.phoenix.ui.home.fragment.HomeMessage;
import com.zhengshuo.phoenix.ui.home.fragment.HomeMy;
import com.zhengshuo.phoenix.ui.home.fragment.HomeSquare;
import com.zhengshuo.phoenix.ui.homemy.activity.Person_Set;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 首页
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0009
 */
public class MainActivity extends BaseBindingActivity<ActivityMainBinding> implements View.OnClickListener {
    int fragmentId = R.id.act_main_fragment;
    protected List<AppMenuBean> menuList = new ArrayList<>();
    int[] ivTabs = new int[]{R.drawable.tab_home, R.drawable.tab_square, R.mipmap.homepage_add, R.drawable.tab_mes, R.drawable.tab_my};

    @Override
    protected void initView() {
        initMenuData();
    }

    private void initMenuData() {
        ArrayList<String> tabName = new ArrayList<>();
        tabName.add("首页");
        tabName.add("广场");
        tabName.add("");
        tabName.add("消息");
        tabName.add("我的");
        for (int i = 0; i < tabName.size(); i++) {
            AppMenuBean bean = new AppMenuBean();
            bean.setName(tabName.get(i));
            menuList.add(bean);
        }
        initViewPager(0);

        addFragment(new Home());
        addFragment(new HomeSquare());
        addFragment(new HomeMessage());
        addFragment(new HomeMy());
        showFragment(0, fragmentId);
    }

    @Override
    protected void initEvent() {
        getBinding().mTab.addOnTabSelectedListener(new MyOnTabSelectedListener());
        getBinding().setTv.setOnClickListener(this);
    }

    protected void initViewPager(int position) {
        TabLayout mTabLayout = getBinding().mTab;
        mTabLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.homeTab_color_80));
        int size = menuList.size();
        for (int i = 0; i < size; i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            if (tab != null) {
                String name = menuList.get(i).getName();
                tab.setCustomView(getTabView(i, name));
//                tab.setText(name);
                mTabLayout.addTab(tab);
            }
        }
    }

    /**
     * 自定义Tab的View 初始化控件 颜色
     * @param currentPosition
     * @return
     */
    private View getTabView(int currentPosition, String tabName) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.main_tab, null);
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        if (TextUtils.equals(tabName, "")) {
            tab_Txt.setBackgroundResource(R.mipmap.homepage_add);
        } else if (TextUtils.equals(tabName, "首页")) {
            setTopBg(tab_Txt, ivTabs[currentPosition]);
        } else {
            setTopBg(tab_Txt, ivTabs[currentPosition], R.color.white);
        }
        tab_Txt.setText(menuList.get(currentPosition).getName());
        return view;
    }

    /**
     * 选中的颜色
     * @param tab
     * @param color
     * @param isSelect
     */
    private void selectStyle(TabLayout.Tab tab, int color, boolean isSelect) {
        View view = tab.getCustomView();
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        int position = tab.getPosition();
        String tabName = menuList.get(tab.getPosition()).getName();
        if (TextUtils.equals(tabName, "")) {//空字符点击的是加号
        } else if (TextUtils.equals(tabName, "首页")) {
            getBinding().mTab.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.homeTab_color_80));
            changeTabView(R.color.white);
            setTopBg(tab_Txt, ivTabs[position]);
        } else {
            getBinding().mTab.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            changeTabView(R.color.text_color_535353);
            setTopBg(tab_Txt, ivTabs[position]);
        }
        tab_Txt.setSelected(isSelect);
        tab_Txt.setTextColor(ContextCompat.getColor(mActivity, color));
    }

    /**
     * 未点击的tab
     * @param tab
     * @param color
     */
    private void unSelectStyle(TabLayout.Tab tab, int color) {
        View view = tab.getCustomView();
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        tab_Txt.setSelected(true);
        int position = tab.getPosition();
        setTopBg(tab_Txt, ivTabs[position]);
        tab_Txt.setTextColor(ContextCompat.getColor(mActivity, color));
    }

    /**
     * tab已初始化切换 修改默认字体颜色
     */
    private void changeTabView(int color) {
        TabLayout mTab = getBinding().mTab;
        for (int i = 0; i < mTab.getTabCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);
            if (tab != null) {
                String name = menuList.get(i).getName();
                tab.setCustomView(getUnTabView(tab, i, name, color));
            }
        }
    }

    private View getUnTabView(TabLayout.Tab tab, int currentPosition, String tabName, int color) {
        View view = tab.getCustomView();
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        tab_Txt.setSelected(false);
        tab_Txt.setTextColor(ContextCompat.getColor(mActivity, color));
        if (!TextUtils.equals(tabName, "")) {
            setTopBg(tab_Txt, ivTabs[currentPosition], color);
        }
        return view;
    }

    /**
     * 修改top图片颜色
     * @param textView
     * @param resourceId
     * @param color
     */
    private void setTopBg(TextView textView, int resourceId, int color) {
        Drawable drawable = getResources().getDrawable(resourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, color));
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    /**
     * 选中颜色
     * @param textView
     * @param resourceId
     */
    private void setTopBg(TextView textView, int resourceId) {
        Drawable drawable = getResources().getDrawable(resourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_Tv:
                closeLeft();
                skipAnotherActivity(Person_Set.class);
                break;
        }
    }

    // 关闭左侧抽屉
    private void closeLeft() {
        getBinding().dlLayout.closeDrawer(getBinding().lvDrawerLeft);
    }


    private class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //在这里可以设置选中状态下  tab字体显示样式
            View view = tab.getCustomView();
            if (null != view) {
                selectStyle(tab, R.color.text_color_4f59ff, true);
                changeFragment(tab);
            }
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            if (null != view) {
                int selectedTabPosition = getBinding().mTab.getSelectedTabPosition();
                String name = menuList.get(selectedTabPosition).getName();
                if (TextUtils.equals(name, "")) {
                    unSelectStyle(tab, R.color.text_color_4f59ff);
                }
            }
        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
        }
    }

    /**
     * 点击切换页面
     * @param tab
     */
    private void changeFragment(TabLayout.Tab tab){
        int position = tab.getPosition();
        String name = menuList.get(position).getName();
        if (TextUtils.equals(name,"")){
            Toast.makeText(mActivity,"点击加号",Toast.LENGTH_SHORT).show();
        }else {
            if (TextUtils.equals(name,"首页")){
                position = 0;
            }else if (TextUtils.equals(name,"广场")){
                position = 1;
            }else if (TextUtils.equals(name,"消息")){
                position = 2;
            }else if (TextUtils.equals(name,"我的")){
                position = 3;
            }
            showFragment(position, fragmentId);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void LeftDL() {
        DrawerLayout dlLayout = getBinding().dlLayout;
        ConstraintLayout lvDrawerLeft = getBinding().lvDrawerLeft;
        if (dlLayout.isDrawerOpen(lvDrawerLeft)) { // 左侧菜单列表已打开
            dlLayout.closeDrawer(lvDrawerLeft); // 关闭左侧抽屉
        } else { // 左侧菜单列表未打开
            dlLayout.openDrawer(lvDrawerLeft); // 打开左侧抽屉
        }
    }

}