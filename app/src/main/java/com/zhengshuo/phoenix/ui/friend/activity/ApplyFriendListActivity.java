package com.zhengshuo.phoenix.ui.friend.activity;

import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.model.FriendApplyBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.friend.adapter.Friend_NewFriendList_Adapter;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.ApplyFriendListViewModel;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;

import java.util.List;
import butterknife.BindView;

/**
 * @Description: 好友申请列表
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class ApplyFriendListActivity extends BaseActivity {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private ApplyFriendListViewModel viewModel;

    private Friend_NewFriendList_Adapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recyleview;
    }

    @Override
    protected void initView() {
        title = "好友申请";
        initRecycerView();
    }

    public void initRecycerView() {
        LinearLayoutManager mManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new Friend_NewFriendList_Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ApplyFriendListViewModel.class);
        viewModel
                .getFriendsAll()
                .observe(
                        this,
                        new Observer<Resource<List<FriendApplyBean>>>() {
                            @Override
                            public void onChanged(Resource<List<FriendApplyBean>> applyFriendListInfoResource) {
                                if (applyFriendListInfoResource.status == Status.SUCCESS
                                        && applyFriendListInfoResource.data != null) {
                                    List<FriendApplyBean> mList = applyFriendListInfoResource.data;
                                    mAdapter.setNewData(mList);
                                } else if (applyFriendListInfoResource.status == Status.ERROR) {
                                    ToastUtil.ss(applyFriendListInfoResource.message);
                                }
                            }
                        });

        // 同意
        viewModel
                .getAgreeResult()
                .observe(
                        this,
                        new Observer<Resource<String>>() {
                            @Override
                            public void onChanged(Resource<String> resource) {
                                // 提示
                                if (resource.status == Status.SUCCESS) {
                                    LiveDataBus.get().with(EventConstants.ADD_NEW_FRIEND).postValue(true);
                                } else if (resource.status == Status.ERROR) {
                                    ToastUtil.ss(resource.message);
                                }
                            }
                        });

        // 拒绝
        viewModel
                .getIngoreResult()
                .observe(
                        this,
                        new Observer<Resource<String>>() {
                            @Override
                            public void onChanged(Resource<String> resource) {
                                // 提示
                                if (resource.status == Status.SUCCESS) {

                                } else if (resource.status == Status.LOADING) {

                                } else {
                                    ToastUtil.ss(resource.message);
                                }
                            }
                        });
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FriendApplyBean obj = (FriendApplyBean) adapter.getItem(position);
                if (obj==null) {
                    return;
                }

                switch (view.getId()){
                    case R.id.refuse_Btn:
                        viewModel.ingore(obj.getFriendApplyId());
                        break;
                    case R.id.add_Btn:
                        viewModel.agree(obj.getFriendApplyId());
                        break;
                }
            }
        });
    }
}
