package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.GroupListTreeBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.task.GroupTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;

/**
 * @Description: 群组列表ViewModel
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0014
 */
public class GroupListViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<GroupListTreeBean>>
            groupsAll;


    private GroupTask groupTask;

    public GroupListViewModel(@NonNull Application application) {
        super(application);
        groupTask = new GroupTask(application);
        groupsAll = new SingleSourceLiveData<>();
        getAllGroupList();
    }

    /** 获取群列表 */
    public void getAllGroupList() {
        groupsAll.setSource(groupTask.getMyGroupList());
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public LiveData<Resource<GroupListTreeBean>> getGroupsAll() {
        return groupsAll;
    }


}
