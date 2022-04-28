package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.FriendApplyBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.task.FriendTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;
import com.zhengshuo.phoenix.util.SingleSourceMapLiveData;

import java.util.List;

/**
 * @Description: 好友申请ViewModel
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class ApplyFriendListViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<List<FriendApplyBean>>>
            friendsAll;

    private SingleSourceMapLiveData<Resource<String>, Resource<String>> agreeResult;

    private SingleSourceMapLiveData<Resource<String>, Resource<String>> ingoreResult;

    private FriendTask friendTask;

    public ApplyFriendListViewModel(@NonNull Application application) {
        super(application);
        friendTask = new FriendTask(application);
        friendsAll = new SingleSourceLiveData<>();

        agreeResult =
                new SingleSourceMapLiveData<>(
                        resource -> {
                            if (resource.status == Status.SUCCESS) {
                                // 成功之后刷新列表
                                getFriendApplyList();
                            }
                            return resource;
                        });

        ingoreResult =
                new SingleSourceMapLiveData<>(
                        resource -> {
                            if (resource.status == Status.SUCCESS) {
                                // 成功之后刷新列表
                                getFriendApplyList();
                            }
                            return resource;
                        });

        getFriendApplyList();

    }

    /** 获取好友申请列表 */
    public void getFriendApplyList() {
        friendsAll.setSource(friendTask.getFriendApplyList());
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public LiveData<Resource<List<FriendApplyBean>>> getFriendsAll() {
        return friendsAll;
    }

    /**
     * 同意添加好友结果
     *
     * @return
     */
    public LiveData<Resource<String>> getAgreeResult() {
        return agreeResult;
    }

    /**
     * 接受好友请求
     *
     * @param friendId
     */
    public void agree(String friendId) {
        agreeResult.setSource(friendTask.agree(friendId));
    }

    /**
     * 忽略好友请求结果
     *
     * @return
     */
    public LiveData<Resource<String>> getIngoreResult() {
        return ingoreResult;
    }

    /**
     * 忽略好友请求
     *
     * @param friendId
     */
    public void ingore(String friendId) {
        ingoreResult.setSource(friendTask.ingore(friendId));
    }

}
