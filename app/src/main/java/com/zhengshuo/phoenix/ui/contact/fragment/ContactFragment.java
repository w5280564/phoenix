package com.zhengshuo.phoenix.ui.contact.fragment;


import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.util.PinyinUtils;
import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.contact.adapter.MyContactList_Adapter;
import com.zhengshuo.phoenix.ui.friend.activity.ApplyFriendListActivity;
import com.zhengshuo.phoenix.ui.chat.activity.ChatActivity;
import com.zhengshuo.phoenix.ui.group.activity.MyGroupListActivity;
import com.zhengshuo.phoenix.util.ListUtil;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.FriendContactListPickViewModel;
import com.lzj.sidebar.SideBarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 通讯录
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0010
 */
public class ContactFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.sideBarLayout)
    SideBarLayout sideBarLayout;
    private FriendContactListPickViewModel mFriendListViewModel;
    private MyContactList_Adapter mMyContactList_Adapter;
    private List<FriendBean> mList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_list;
    }


    @Override
    protected void initView(View mView) {
        initRecyclerView();
    }

    public void initRecyclerView() {
        LinearLayoutManager mManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mMyContactList_Adapter = new MyContactList_Adapter();
        mRecyclerView.setAdapter(mMyContactList_Adapter);
    }

    @Override
    protected void InitViewModel() {
        mFriendListViewModel = new ViewModelProvider(this).get(FriendContactListPickViewModel.class);
        mFriendListViewModel
                .getFriendsAllViewModel()
                .observe(
                        this,
                        new Observer<Resource<List<FriendBean>>>() {
                            @Override
                            public void onChanged(Resource<List<FriendBean>> applyFriendListInfoResource) {
                                if (applyFriendListInfoResource.status == Status.SUCCESS
                                        && applyFriendListInfoResource.data != null) {
                                    List<FriendBean> mTempList = applyFriendListInfoResource.data;
                                    if (!ListUtil.isEmpty(mTempList)) {
                                        mList.addAll(mTempList);
                                        for (FriendBean obj:mList) {
                                            String pinyin = PinyinUtils.getPingYin(obj.getRemarkName());
                                            String sortString = pinyin.substring(0, 1).toUpperCase();
                                            if (sortString.matches("[A-Z]")) {
                                                obj.setInitialLetter(sortString);
                                            } else {
                                                obj.setInitialLetter("#");
                                            }
                                            JSONObject friendJsonObject = (JSONObject) JSONObject.toJSON(obj);
                                            UserManager.get().saveUserInfo(friendJsonObject);
                                        }

                                        sortList();
                                        mMyContactList_Adapter.setNewData(mList);
                                    }

                                } else if (applyFriendListInfoResource.status == Status.ERROR) {
                                    ToastUtil.ss(applyFriendListInfoResource.message);
                                }
                            }
                        });

        mFriendListViewModel.contactChangeObservable().with(EventConstants.ADD_NEW_FRIEND, Boolean.class).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAddNewFriend) {
                if (isAddNewFriend) {
                    mFriendListViewModel.getFriendsAllList();
                }
            }
        });
    }


    /**
     * 排序
     */
    private void sortList() {
        Collections.sort(mList, new Comparator<FriendBean>() {
            @Override
            public int compare(FriendBean lhs, FriendBean rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    String name = rhs.getRemarkName();
                    return name.compareTo(name);
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
            }
        });
    }


    @Override
    protected void initEvent() {
        mMyContactList_Adapter.setOnItemClickListener(this);

        sideBarLayout.setSideBarLayout(new SideBarLayout.OnSideBarLayoutListener() {
            @Override
            public void onSideBarScrollUpdateItem(String word) {
                for (int i = 0; i < mList.size(); i++) {
                    if (word.equals(mList.get(i).getInitialLetter())) {
                        mRecyclerView.scrollToPosition(i);
                        LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();//必不可少
                        mLayoutManager.scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        });
    }


    @OnClick({R.id.cl_new_friend,R.id.cl_group})
    public void onClick(View mView) {
        switch (mView.getId()){
            case R.id.cl_new_friend:
                skipAnotherActivity(ApplyFriendListActivity.class);
                break;
            case R.id.cl_group:
                skipAnotherActivity(MyGroupListActivity.class);
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FriendBean obj = (FriendBean) adapter.getItem(position);
        if (obj==null) {
            return;
        }

        ChatActivity.actionStart(mActivity,obj.getImId(), MessageUtils.CHAT_SINGLE);
    }
}
