package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alibaba.fastjson.JSONArray;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.task.GroupTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;

/**
 * @Description: 群详情ViewModel
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class GroupDetailViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<JSONArray>>
            groupsAll;


    private GroupTask groupTask;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        groupTask = new GroupTask(application);
        groupsAll = new SingleSourceLiveData<>();
    }

    /** 获取群成员列表 */
    public void getGroupUserList(String groupId) {
        groupsAll.setSource(groupTask.getGroupUserList(groupId));
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public LiveData<Resource<JSONArray>> getGroupUserListViewModel() {
        return groupsAll;
    }

}
