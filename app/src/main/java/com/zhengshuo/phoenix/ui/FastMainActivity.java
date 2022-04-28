package com.zhengshuo.phoenix.ui;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.FragmentKeyEventListener;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.ui.contact.fragment.ContactFragment;
import com.zhengshuo.phoenix.ui.conversation.ConversationFragment;
import com.zhengshuo.phoenix.ui.friend.activity.SearchFriendActivity;
import com.zhengshuo.phoenix.ui.group.activity.CreateGroupStepOneActivity;
import com.zhengshuo.phoenix.ui.mine.MeFragment;
import com.zhengshuo.phoenix.util.DisplayUtil;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.widget.CustomTitleBar;
import com.zhengshuo.phoenix.widget.MainBottomTabGroupView;
import com.zhengshuo.phoenix.widget.MainBottomTabItem;
import com.zhengshuo.phoenix.widget.MorePopWindow;
import com.zhengshuo.phoenix.widget.TabItem;
import com.zhengshuo.phoenix.widget.ViewPagerNoScroll;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Description: 首页
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0009
 */
public class FastMainActivity extends BaseActivity implements MorePopWindow.OnPopWindowItemClickListener, ConversationFragment.NewMessageListener {
    @BindView(R.id.vp_main_container)
    ViewPagerNoScroll vpMainContainer;
    @BindView(R.id.tg_bottom_tabs)
    MainBottomTabGroupView tgBottomTabs;
    @BindView(R.id.title_bar)
    CustomTitleBar title_bar;
    /**
     * 各个 Fragment 界面
     */
    private List<Fragment> fragments = new ArrayList<>();



    /** tab 项枚举 */
    public enum Tab {
        /** 聊天 */
        CHAT(0),
        /** 联系人 */
        CONTACTS(1),
        /** 我的 */
        ME(2);

        private int value;

        Tab(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Tab getType(int value) {
            for (Tab type : Tab.values()) {
                if (value == type.getValue()) {
                    return type;
                }
            }
            return null;
        }
    }

    /** tabs 的图片资源 */
    private int[] tabImageRes =
            new int[] {
                    R.drawable.tab_chat_selector,
                    R.drawable.tab_contact_list_selector,
                    R.drawable.tab_me_selector
            };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_fast;
    }


    @Override
    protected void initView() {
        initTabs();
        initFragmentViewPager();
        // 设置当前的选项为聊天界面
        tgBottomTabs.setSelected(Tab.CHAT.value);
    }


    @Override
    public void onRightClick() {
        int currentItem = vpMainContainer.getCurrentItem();
        if (currentItem == Tab.CHAT.getValue()) {
            MorePopWindow morePopWindow =
                    new MorePopWindow(FastMainActivity.this, FastMainActivity.this);
            morePopWindow.showPopupWindow(title_bar.getRightLayout(), 1f);
        } else if (currentItem == Tab.CONTACTS.getValue()) {
            onAddFriendClick();
        }
    }

    /** 初始化 Tabs */
    private void initTabs() {
        // 初始化 tab
        List<TabItem> items = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.tab_names);
        for (Tab tab : Tab.values()) {
            TabItem tabItem = new TabItem();
            tabItem.id = tab.getValue();
            tabItem.text = stringArray[tab.getValue()];
            tabItem.drawable = tabImageRes[tab.getValue()];
            items.add(tabItem);
        }

        tgBottomTabs.initView(
                items,
                new MainBottomTabGroupView.OnTabSelectedListener() {
                    @Override
                    public void onSelected(View view, TabItem item) {
                        // 当点击 tab 的后， 也要切换到正确的 fragment 页面
                        int currentItem = vpMainContainer.getCurrentItem();
                        if (currentItem != item.id) {
                            // 切换布局
                            vpMainContainer.setCurrentItem(item.id);
                        }

                        if(item.id == Tab.ME.getValue()) {
                            // 如果是我的页面， 则隐藏红点
                            ((MainBottomTabItem) tgBottomTabs.getView(Tab.ME.getValue()))
                                    .setRedVisibility(View.GONE);
                            title_bar.setTitle(UserManager.get().getMyNick());
                            title_bar.hideRight();
                        }else if (item.id == Tab.CHAT.getValue()) {
                            title_bar.setTitle(getResources().getStringArray(R.array.tab_names)[0]);
                            title_bar.showRight();
                            title_bar.setRightImg(R.mipmap.jiahao_top);
                        } else if (item.id == Tab.CONTACTS.getValue()) {
                            title_bar.setTitle(getResources().getStringArray(R.array.tab_names)[1]);
                            title_bar.hideRight();
                        }
                    }
                });

    }

    /**
     * 初始化 initFragmentViewPager
     */
    private void initFragmentViewPager() {
        fragments.add(new ConversationFragment());
        fragments.add(new ContactFragment());
        fragments.add(new MeFragment());

        // ViewPager 的 Adpater
        FragmentPagerAdapter fragmentPagerAdapter =
                new FragmentPagerAdapter(
                        getSupportFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    @Override
                    public Fragment getItem(int position) {
                        return fragments.get(position);
                    }

                    @Override
                    public int getCount() {
                        return fragments.size();
                    }
                };

        vpMainContainer.setAdapter(fragmentPagerAdapter);
        vpMainContainer.setOffscreenPageLimit(fragments.size());
        // 设置页面切换监听
        vpMainContainer.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(
                            int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // 当页面切换完成之后， 同时也要把 tab 设置到正确的位置
                        tgBottomTabs.setSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private int getXOffset() {
        int marginEnd = DisplayUtil.dip2px(FastMainActivity.this, 15);
        float popSelfXOffset =
                getResources().getDimension(R.dimen.main_title_popup_width)
                        - title_bar.getRightLayout().getWidth();
        return (int) (popSelfXOffset);
    }


    @Override
    public void onStartChartClick() {

    }

    @Override
    public void onCreateGroupClick() {
        skipAnotherActivity(CreateGroupStepOneActivity.class);
    }

    @Override
    public void onAddFriendClick() {
        skipAnotherActivity(SearchFriendActivity.class);
    }

    @Override
    public void onScanClick() {

    }


    /**
     * 返回多少条未读消息
     * @param count
     */
    @Override
    public void onUnReadMessages(int count) {
        LogUtil.d("未读消息数>>>"+count);
        MainBottomTabItem chatTab = tgBottomTabs.getView(Tab.CHAT.getValue());
        if (count == 0) {
            chatTab.setNumVisibility(View.GONE);
        } else if (count > 0 && count < 20000) {
            chatTab.setNumVisibility(View.VISIBLE);
            chatTab.setNum(String.valueOf(count));
        } else {
            chatTab.setVisibility(View.VISIBLE);
            chatTab.setNum(
                    getString(
                            R.string.main_chat_tab_more_read_message));
        }
    }

    private FragmentKeyEventListener fragmentKeyeventListener;


    public void setFragmentKeyEventListener(FragmentKeyEventListener fragmentKeyEventListener) {
        this.fragmentKeyeventListener = fragmentKeyEventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (fragmentKeyeventListener!=null) {
            fragmentKeyeventListener.onFragmentKeyEvent(ev.getX());
        }
        return super.dispatchTouchEvent(ev);
    }

}