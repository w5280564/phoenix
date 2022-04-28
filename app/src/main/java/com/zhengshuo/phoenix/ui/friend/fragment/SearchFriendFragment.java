package com.zhengshuo.phoenix.ui.friend.fragment;

import android.widget.EditText;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.ui.interfaces.OnSearchFriendClickListener;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 搜索页
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0010
 */
public class SearchFriendFragment extends BaseFragment {
    @BindView(R.id.search_phone)
    EditText searchPhone;
    private OnSearchFriendClickListener onSearchFriendClick;

    public void setOnSearchFriendClickListener(OnSearchFriendClickListener onSearchFriendClick) {
        this.onSearchFriendClick = onSearchFriendClick;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_friend;
    }




    @OnClick(R.id.search_search)
    public void onClick() {
        String phoneNumber = StringUtil.getEditText(searchPhone);
        if (StringUtil.isBlank(phoneNumber)) {
            ToastUtil.ss("请输入手机号");
            return;
        }

        if (onSearchFriendClick!=null) {
            onSearchFriendClick.onSearchClick(phoneNumber);
        }

    }
}
