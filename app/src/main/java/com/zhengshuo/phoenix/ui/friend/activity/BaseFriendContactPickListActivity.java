package com.zhengshuo.phoenix.ui.friend.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.lzj.sidebar.SideBarLayout;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.group.adapter.GroupPickContactsAdapter;
import com.zhengshuo.phoenix.util.ListUtil;
import com.zhengshuo.phoenix.util.PinyinUtils;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.FriendContactListPickViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;

/**
 * 好友通讯录基类
 */
public class BaseFriendContactPickListActivity extends BaseActivity implements GroupPickContactsAdapter.OnSelectListener {
    @BindView(R.id.query)
    EditText query;
    @BindView(R.id.search_clear)
    ImageButton searchClear;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.toast_dialog)
    TextView toast_dialog;
    @BindView(R.id.sideBarLayout)
    SideBarLayout sideBarLayout;
    protected GroupPickContactsAdapter adapter;
    private String keyword;
    private List<FriendBean> mList = new ArrayList<>();
    private FriendContactListPickViewModel viewModel;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_group_pick_contacts;
    }



    @Override
    protected void initView() {
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GroupPickContactsAdapter();
        rvList.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        adapter.setOnSelectListener(this);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                if (!TextUtils.isEmpty(keyword)) {
                    searchClear.setVisibility(View.VISIBLE);
                    viewModel.getSearchFriends(keyword);
                } else {
                    searchClear.setVisibility(View.INVISIBLE);
                    adapter.setNewData(mList);
                }
            }
        });
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                adapter.setNewData(mList);
            }
        });

        sideBarLayout.setSideBarLayout(new SideBarLayout.OnSideBarLayoutListener() {
            @Override
            public void onSideBarScrollUpdateItem(String word) {
                for (int i = 0; i < mList.size(); i++) {
                    if (word.equals(mList.get(i).getInitialLetter())) {
                        rvList.scrollToPosition(i);
                        LinearLayoutManager mLayoutManager = (LinearLayoutManager) rvList.getLayoutManager();//必不可少
                        mLayoutManager.scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(FriendContactListPickViewModel.class);
        viewModel.getFriendsAllViewModel().observe(this, new Observer<Resource<List<FriendBean>>>() {
            @Override
            public void onChanged(Resource<List<FriendBean>> listResource) {
                if (listResource.status == Status.SUCCESS
                        && listResource.data != null) {
                    List<FriendBean> mTempList = listResource.data;
                    if (!ListUtil.isEmpty(mTempList)) {
                        mList.addAll(mTempList);
                        sideBarLayout.setVisibility(View.VISIBLE);
                        for (FriendBean obj:mList) {
                            String pinyin = PinyinUtils.getPingYin(obj.getRemarkName());
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            if (sortString.matches("[A-Z]")) {
                                obj.setInitialLetter(sortString);
                            } else {
                                obj.setInitialLetter("#");
                            }
                            JSONObject friendJsonObject = (com.alibaba.fastjson.JSONObject) JSONObject.toJSON(obj);
                            UserManager.get().saveUserInfo(friendJsonObject);
                        }
                        sortList();
                    }else{
                        sideBarLayout.setVisibility(View.GONE);
                    }
                    adapter.setNewData(mList);
                } else if (listResource.status == Status.ERROR) {
                    ToastUtil.ss(listResource.message);
                }
            }
        });
    }



    @Override
    public void onSelected(View v, List<String> selectedMembers) {
        titleBar.setRightText("继续(" + selectedMembers.size() + ")");
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

}
