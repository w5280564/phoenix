package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.model.GroupInfoBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.task.FriendTask;
import com.zhengshuo.phoenix.task.GroupTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 好友通讯录创建群组
 * @Author: ouyang
 * @CreateDate: 2022/3/17 0017
 */
public class FriendContactListPickViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<List<FriendBean>>>
            friendsAll;
    private SingleSourceLiveData<Resource<GroupInfoBean>>
            createGroup;
    private SingleSourceLiveData<Resource<List<FriendBean>>>
            searchFriends;
    private FriendTask friendTask;
    private GroupTask groupTask;

    public FriendContactListPickViewModel(@NonNull Application application) {
        super(application);
        friendTask = new FriendTask(application);
        groupTask = new GroupTask(application);
        friendsAll = new SingleSourceLiveData<>();
        createGroup = new SingleSourceLiveData<>();
        searchFriends = new SingleSourceLiveData<>();
        getFriendsAllList();
    }

    /** 获取通讯录好友列表 */
    public void getFriendsAllList() {
        friendsAll.setSource(friendTask.getFriendList());
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public LiveData<Resource<List<FriendBean>>> getFriendsAllViewModel() {
        return friendsAll;
    }


    public LiveDataBus contactChangeObservable() {
        return LiveDataBus.get();
    }


    /** 创建群组 */
    public void createGroup(String groupName,String groupIntroduction, ArrayList<String> userIds) {
        createGroup.setSource(groupTask.createGroup(groupName,groupIntroduction,userIds));
    }

    /**
     * 创建群组
     *
     * @return
     */
    public LiveData<Resource<GroupInfoBean>> getCreateGroupViewModel() {
        return createGroup;
    }

    /**
     * 搜索好友
     * @return
     */
    public LiveData<Resource<List<FriendBean>>> getSearchFriends(String keyword) {
        return searchFriends;
    }
}
