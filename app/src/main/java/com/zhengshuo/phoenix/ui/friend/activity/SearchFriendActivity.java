package com.zhengshuo.phoenix.ui.friend.activity;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.ui.interfaces.OnSearchFriendClickListener;
import com.zhengshuo.phoenix.ui.interfaces.OnSearchFriendItemClickListener;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.friend.fragment.SearchFriendFragment;
import com.zhengshuo.phoenix.ui.friend.fragment.SearchFriendResultFragment;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.SearchFriendNetViewModel;


/**
 * @Description: 搜索好友页
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0010
 */
public class SearchFriendActivity extends BaseActivity implements OnSearchFriendClickListener, OnSearchFriendItemClickListener {
    private SearchFriendFragment searchFriendFragment;
    private SearchFriendResultFragment searchFriendResultFragment;
    private SearchFriendNetViewModel viewModel;
    private boolean isFriend;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_search_content;
    }

    @Override
    protected void initView() {
        searchFriendFragment = new SearchFriendFragment();
        searchFriendFragment.setOnSearchFriendClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_fragment_container, searchFriendFragment)
                .commit();

    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchFriendNetViewModel.class);
        viewModel
                .getSearchFriend()
                .observe(
                        this,
                        new Observer<Resource<SearchFriendInfoBean>>() {
                            @Override
                            public void onChanged(Resource<SearchFriendInfoBean> searchFriendInfoResource) {
                                if (searchFriendInfoResource.status == Status.SUCCESS
                                        && searchFriendInfoResource.data != null) {
                                    searchFriendResultFragment = new SearchFriendResultFragment();
                                    searchFriendResultFragment.setData(SearchFriendActivity.this,
                                            searchFriendInfoResource.data);
                                    pushFragment(searchFriendResultFragment);
                                } else if (searchFriendInfoResource.status == Status.ERROR) {
                                    ToastUtil.ss("用户不存在");
                                }
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pushFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_fragment_container, searchFriendResultFragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onSearchClick(String searchContent) {
        viewModel.searchFriendFromServer(searchContent);
    }

    @Override
    public void onLeftClick() {
        hideKeyboard();
        onBackPressed();
    }

    @Override
    public void onSearchFriendItemClick(SearchFriendInfoBean searchFriendInfoBean) {
            toDetailActivity(searchFriendInfoBean);
    }

    private void toDetailActivity(SearchFriendInfoBean searchFriendInfoBean) {
        AddFriendDetailActivity.actionStart(mActivity, searchFriendInfoBean);
    }

}
