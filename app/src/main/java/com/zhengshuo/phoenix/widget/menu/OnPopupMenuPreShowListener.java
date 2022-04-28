package com.zhengshuo.phoenix.widget.menu;


public interface OnPopupMenuPreShowListener {
    /**
     * popupMenu展示前的监听，可以对PopupMenu进行设置
     * @param menuHelper {@link PopupMenuHelper}
     * @param position 条目位置
     */
    void onMenuPreShow(PopupMenuHelper menuHelper, int position);
}

