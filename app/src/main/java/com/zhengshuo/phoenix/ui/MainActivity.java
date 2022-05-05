package com.zhengshuo.phoenix.ui;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.tabs.TabLayout;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.base.PhotoFragment;
import com.zhengshuo.phoenix.base.baseview.NormalFragmentAdapter;
import com.zhengshuo.phoenix.databinding.ActivityMainBinding;
import com.zhengshuo.phoenix.model.AppMenuBean;
import com.zhengshuo.phoenix.ui.homemy.fragment.HomeMy;
import com.zhengshuo.phoenix.ui.mine.MeFragment;
import com.zhengshuo.phoenix.util.DisplayUtil;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 首页
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0009
 */
public class MainActivity extends BaseBindingActivity<ActivityMainBinding> {
    protected List<AppMenuBean> menuList = new ArrayList<>();
    protected List<Fragment> fragments = new ArrayList<>();
    int[] ivTabs = new int[]{R.drawable.tab_home, R.drawable.tab_home, R.mipmap.homepage_add, R.drawable.tab_home, R.drawable.tab_home};

    @Override
    protected void initView() {
//        getBinding().tabFind.setBackgroundTintMode(PorterDuff.Mode.ADD);
//        getBinding().tabGroup.setOnCheckedChangeListener(new tabClick());
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
        fragments.add(new HomeMy());
        fragments.add(new HomeMy());
        fragments.add(new HomeMy());
        fragments.add(new HomeMy());
        fragments.add(new HomeMy());
        initViewPager(0);
    }

    @Override
    protected void initEvent() {
        getBinding().mTab.addOnTabSelectedListener(new MyOnTabSelectedListener());
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private class tabClick implements RadioGroup.OnCheckedChangeListener {
//        @SuppressLint("NonConstantResourceId")
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            switch (checkedId) {
////                case R.id.tab_home:
////                    ToastUtil.s("点击首页",2000);
////                    getBinding().tabHome.setChecked(true);
////                    break;
//
//            }
//        }
//    }

    /**
     * 添加右上角的红点
     */
    private void addTabBadge() {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mBottomNavigationView.getChildAt(0);
//        BottomNavigationItemView itemTab = (BottomNavigationItemView) menuView.getChildAt(3);
//        RadioGroup tabGroup = getBinding().tabGroup;
//        RadioButton tabMes = getBinding().tabMes;
//        View badge = LayoutInflater.from(mContext).inflate(R.layout.layout_home_badge, tabGroup, false);
//        tv_unread_msg_number = badge.findViewById(R.id.tv_unread_msg_number);
//        tabMes.addView(badge);
    }


    protected void initViewPager(int position) {
        TabLayout mTabLayout = getBinding().mTab;
        ViewPager mViewPager = getBinding().viewpager;

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
//        if (null != view) {
//            setTextViewStyle(view,  R.color.text_color_444444, false);
//        }
    }

    private void setTextViewStyle(View view, int color,boolean isSelect) {
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        tab_Txt.setTextColor(ContextCompat.getColor(mActivity, color));
        tab_Txt.setSelected(isSelect);
//        TextView mTextView = view.findViewById(R.id.tab_item_textview);
//        View line = view.findViewById(R.id.line);
//        mTextView.setTextSize(size);
//        mTextView.setTypeface(textStyle);
//        line.setVisibility(visibility);
    }



    /**
     * 自定义Tab的View
     *
     * @param currentPosition
     * @return
     */
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.main_tab, null);
//        TextView textView = view.findViewById(R.id.tab_item_textview);
//        textView.setTextSize(normalSize);
        TextView tab_Txt = view.findViewById(R.id.tab_Txt);
        setTopBg(tab_Txt, ivTabs[currentPosition], R.color.white);
        tab_Txt.setText(menuList.get(currentPosition).getName());
        return view;
    }

    /**
     * 修改按钮背景色
     * @param button
     * @param drawable
     * @param color
     */
    public void setTint(Button button, int drawable, int color) {
        Drawable originalDrawable = ContextCompat.getDrawable(this, drawable);
        Drawable wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate();
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, color));
        button.setBackground(wrappedDrawable);
    }

    /**
     * 修改top图片颜色
     * @param textView
     * @param resourceId
     * @param color
     */
    private void setTopBg(TextView textView,int resourceId,int color){
        Drawable drawable = getResources().getDrawable(resourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, color));
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    private class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //在这里可以设置选中状态下  tab字体显示样式
            View view = tab.getCustomView();
            if (null != view) {
                setTextViewStyle(view,  R.color.text_color_4f59ff, true);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            if (null != view) {
                setTextViewStyle(view, R.color.text_color_535353, false);
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}