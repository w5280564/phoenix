package com.zhengshuo.phoenix.ui.group.activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baozi.treerecyclerview.adpater.TreeRecyclerAdapter;
import com.baozi.treerecyclerview.adpater.TreeRecyclerType;
import com.baozi.treerecyclerview.factory.ItemHelperFactory;
import com.baozi.treerecyclerview.item.TreeItem;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.model.GroupListTreeBean;
import com.zhengshuo.phoenix.model.MyGroupBigBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.GroupListViewModel;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * @Description: 群组列表
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0014
 */
public class MyGroupListActivity extends BaseActivity {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private GroupListViewModel viewModel;
    private TreeRecyclerAdapter treeRecyclerAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_group;
    }

    @Override
    protected void initView() {
        title = "我的群组";
        initRecyclerView();
    }

    public void initRecyclerView() {
        LinearLayoutManager mManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //可折叠
        treeRecyclerAdapter = new TreeRecyclerAdapter(TreeRecyclerType.SHOW_EXPAND);
        mRecyclerView.setAdapter(treeRecyclerAdapter);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        viewModel
                .getGroupsAll()
                .observe(
                        this,
                        new Observer<Resource<GroupListTreeBean>>() {
                            @Override
                            public void onChanged(Resource<GroupListTreeBean> applyFriendListInfoResource) {
                                if (applyFriendListInfoResource.status == Status.SUCCESS
                                        && applyFriendListInfoResource.data != null) {
                                    GroupListTreeBean obj = applyFriendListInfoResource.data;
                                    List<MyGroupBigBean> mList = new ArrayList<>();
                                    MyGroupBigBean bigObj1 = new MyGroupBigBean();
                                    bigObj1.setCount(obj.getJoinGroupList().size());
                                    bigObj1.setListName("我加入的群");
                                    bigObj1.setGroupList(obj.getJoinGroupList());

                                    MyGroupBigBean bigObj2 = new MyGroupBigBean();
                                    bigObj2.setCount(obj.getManageGroupList().size());
                                    bigObj2.setListName("我管理的群");
                                    bigObj2.setGroupList(obj.getManageGroupList());

                                    MyGroupBigBean bigObj3 = new MyGroupBigBean();
                                    bigObj3.setCount(obj.getOwnGroupList().size());
                                    bigObj3.setListName("我创建的群");
                                    bigObj3.setGroupList(obj.getOwnGroupList());
                                    mList.add(bigObj1);
                                    mList.add(bigObj2);
                                    mList.add(bigObj3);
                                    //创建item
                                    //新的
                                    List<TreeItem> items = ItemHelperFactory.createItems(mList);
                                    //添加到adapter
                                    treeRecyclerAdapter.getItemManager().replaceAllItem(items);
                                } else if (applyFriendListInfoResource.status == Status.ERROR) {
                                    ToastUtil.ss(applyFriendListInfoResource.message);
                                }
                            }
                        });

    }

    @Override
    protected void initEvent() {

    }
}
