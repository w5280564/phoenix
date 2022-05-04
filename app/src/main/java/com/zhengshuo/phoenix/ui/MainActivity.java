package com.zhengshuo.phoenix.ui;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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
        TabLayout mTab = getBinding().mTab;
        ViewPager viewpager = getBinding().viewpager;
        initViewPager(mTab, viewpager, 0);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class tabClick implements RadioGroup.OnCheckedChangeListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
//                case R.id.tab_home:
//                    ToastUtil.s("点击首页",2000);
//                    getBinding().tabHome.setChecked(true);
//                    break;

            }
        }
    }

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


    protected void initViewPager(TabLayout mTabLayout, ViewPager mViewPager, int position) {
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
            setTextViewStyle(view, 12, R.color.text_color_444444, Typeface.DEFAULT_BOLD, View.VISIBLE);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
//        textView.setTextSize(normalSize);
        textView.setText(menuList.get(currentPosition).getName());
        return view;
    }

}