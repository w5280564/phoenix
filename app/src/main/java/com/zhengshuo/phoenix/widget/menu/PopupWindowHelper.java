package com.zhengshuo.phoenix.widget.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.util.DisplayUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PopupWindowHelper {
    private static final int[] itemIds = {R.id.action_id_play,R.id.action_id_copy, R.id.action_id_recall,R.id.action_id_forward, R.id.action_id_yinyong, R.id.action_id_delete};
    private static final int[] titles = {R.string.action_play_receiver,R.string.action_copy, R.string.action_recall,R.string.action_forward, R.string.action_yinyong, R.string.action_delete};
    private static final int[] icons = {R.mipmap.tingtong,R.mipmap.fuzhi, R.mipmap.chehui,R.mipmap.zhuanfa, R.mipmap.yingyong, R.mipmap.shanchu};
    private static final int SPAN_COUNT = 5;
    private FastPopupWindow pMenu;
    private List<MenuItemBean> menuItems = new ArrayList<>();
    private Map<Integer, MenuItemBean> menuItemMap = new HashMap<>();
    private TextView tvTitle;
    private RecyclerView rvMenuList;
    private Context context;
    private MenuAdapter adapter;
    private FastPopupWindow.OnPopupWindowItemClickListener itemClickListener;
    private FastPopupWindow.OnPopupWindowDismissListener dismissListener;
    private boolean touchable;
    private Drawable background;
    private View layout;

    public PopupWindowHelper() {
        if(pMenu != null) {
            pMenu.dismiss();
        }
        menuItems.clear();
        menuItemMap.clear();
    }

    /**
     * @param context
     */
    public void initMenu(@NonNull Context context) {
        this.context = context;
        pMenu = new FastPopupWindow(context, true);
        layout = LayoutInflater.from(context).inflate(R.layout.layout_menu_popupwindow, null);
        pMenu.setContentView(layout);
        tvTitle = layout.findViewById(R.id.tv_title);
        rvMenuList = layout.findViewById(R.id.rv_menu_list);
        adapter = new MenuAdapter();
        rvMenuList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismiss();
                if(itemClickListener != null) {
                    itemClickListener.onMenuItemClick((MenuItemBean) adapter.getItem(position));
                }
            }
        });
    }

    public void clear() {
        menuItems.clear();
        menuItemMap.clear();
    }

    public void setDefaultMenus() {
        MenuItemBean bean;
        for(int i = 0; i < itemIds.length; i++) {
            bean = new MenuItemBean(0, itemIds[i], (i+1)*10, context.getString(titles[i]));
            bean.setResourceId(icons[i]);
            addItemMenu(bean);
        }
    }

    public void addItemMenu(MenuItemBean item) {
        if(!menuItemMap.containsKey(item.getItemId())) {
            menuItemMap.put(item.getItemId(), item);
        }
    }

    public void addItemMenu(int groupId, int itemId, int order, String title) {
        MenuItemBean item = new MenuItemBean(groupId, itemId, order, title);
        addItemMenu(item);
    }

    public MenuItemBean findItem(int id) {
        if(menuItemMap.containsKey(id)) {
            return menuItemMap.get(id);
        }
        return null;
    }

    public void findItemVisible(int id, boolean visible) {
        if(menuItemMap.containsKey(id)) {
            menuItemMap.get(id).setVisible(visible);
        }
    }

    public void setOutsideTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    private void showPre() {
        pMenu.setOutsideTouchable(touchable);
        pMenu.setBackgroundDrawable(background);
        checkIfShowItems();
        sortList(menuItems);
        adapter.setNewData(menuItems);
    }

    private void sortList(List<MenuItemBean> menuItems) {
        Collections.sort(menuItems, new Comparator<MenuItemBean>() {
            @Override
            public int compare(MenuItemBean o1, MenuItemBean o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();
                if(order2 < order1) {
                    return 1;
                }else if(order1 == order2) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    private void checkIfShowItems() {
        if(menuItemMap.size() > 0) {
            menuItems.clear();
            Iterator<MenuItemBean> iterator = menuItemMap.values().iterator();
            while (iterator.hasNext()) {
                MenuItemBean item = iterator.next();
                if(item.isVisible()) {
                    menuItems.add(item);
                }
            }
        }
    }

    public void showTitle(@NonNull String title) {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void show(View parent, View v) {
        showPre();
        //根据条目选择spanCount
        if(menuItems.size() <= 0) {
            Log.e("EasePopupWindowHelper", "Span count should be at least 1. Provided " + menuItems.size());
            return;
        }
        if(menuItems.size() < SPAN_COUNT) {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, menuItems.size(), RecyclerView.VERTICAL, false));
        }else {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT, RecyclerView.VERTICAL, false));
        }
        getView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = getView().getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = getView().getMeasuredHeight();  //获取测量后的高度

        //获取依附view的坐标
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        //获取父布局的坐标
        int[] location2 = new int[2];
        parent.getLocationOnScreen(location2);

        //设定与依附view之间的间距
        int margin = (int) DisplayUtil.dip2px(context, 5);

        int yOffset = 0;
        if(location[1] - popupHeight - margin < location2[1]) {
            yOffset = location[1] + v.getHeight() + margin;
        }else {
            yOffset = location[1] - popupHeight - margin;

        }
        int xOffset = 0;
        if(location[0] + v.getWidth() / 2 + popupWidth / 2 + DisplayUtil.dip2px(context, 10) > parent.getWidth()) {
            xOffset = (int) (parent.getWidth() - DisplayUtil.dip2px(context, 10) - popupWidth);
        }else {
            xOffset = location[0] + v.getWidth() / 2 - popupWidth / 2;
        }
        //增加对左侧的判断
        if(xOffset < DisplayUtil.dip2px(context, 10)) {
            xOffset = (int) DisplayUtil.dip2px(context, 10);
        }
        pMenu.showAtLocation(v, Gravity.NO_GRAVITY, xOffset, yOffset);
    }

    public void dismiss() {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        pMenu.dismiss();
        if(dismissListener != null) {
            dismissListener.onDismiss(pMenu);
        }
    }


    /**
     * 设置条目点击事件
     * @param listener
     */
    public void setOnPopupMenuItemClickListener(FastPopupWindow.OnPopupWindowItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 监听PopupMenu dismiss事件
     * @param listener
     */
    public void setOnPopupMenuDismissListener(FastPopupWindow.OnPopupWindowDismissListener listener) {
        this.dismissListener = listener;
    }

    public PopupWindow getPopupWindow() {
        return pMenu;
    }

    public View getView() {
        return layout;
    }

    private class MenuAdapter extends BaseQuickAdapter<MenuItemBean, BaseViewHolder> {

        public MenuAdapter() {
            super(R.layout.layout_item_menu_popupwindow);
        }


        @Override
        protected void convert(@NonNull BaseViewHolder helper, MenuItemBean item) {
            ImageView ivActionIcon = helper.getView(R.id.iv_action_icon);
            TextView tvActionName = helper.getView(R.id.tv_action_name);

            String title = item.getTitle();
            if(!TextUtils.isEmpty(title)) {
                tvActionName.setText(title);
            }
            if(item.getResourceId() != 0) {
                ivActionIcon.setImageResource(item.getResourceId());
            }
        }

    }
}

