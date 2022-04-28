package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.task.FriendTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;
import com.zhengshuo.phoenix.util.SingleSourceMapLiveData;

/**
 * @Description: 好友添加ViewModel
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class SearchFriendNetViewModel extends AndroidViewModel {
    private FriendTask friendTask;
    private SingleSourceLiveData<Resource<SearchFriendInfoBean>> searchFriend;
    private SingleSourceMapLiveData<Resource<String>, Resource<String>> addFriend;

    public SearchFriendNetViewModel(@NonNull Application application) {
        super(application);
        friendTask = new FriendTask(application);
        searchFriend = new SingleSourceLiveData<>();
        addFriend =
                new SingleSourceMapLiveData<>(
                        new Function<Resource<String>, Resource<String>>() {
                            @Override
                            public Resource<String> apply(
                                    Resource<String> input) {
                                if (input.status == Status.SUCCESS) {

                                }
                                return input;
                            }
                        });
    }

    public void searchFriendFromServer(String phone) {
        searchFriend.setSource(friendTask.searchFriend(phone));
    }

    public LiveData<Resource<SearchFriendInfoBean>> getSearchFriend() {
        return searchFriend;
    }


    public void inviteFriend(String userId, String message) {
        addFriend.setSource(friendTask.applyFriend(userId, message));
    }

    public LiveData<Resource<String>> getAddFriend() {
        return addFriend;
    }

}
