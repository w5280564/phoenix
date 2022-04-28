package com.zhengshuo.phoenix.net.service;

import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.FriendApplyBean;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.net.YuRuanTalkUrl;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @Description: FriendService
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public interface FriendService {

    @POST(YuRuanTalkUrl.FRIEND_SEARCH)
    LiveData<BaseResultBean<SearchFriendInfoBean>> searchFriend(@Body RequestBody body);

    @POST(YuRuanTalkUrl.APPLY_FRIEND)
    LiveData<BaseResultBean<String>> applyFriend(@Body RequestBody body);

    @POST(YuRuanTalkUrl.FRIEND_APPLY_LIST)
    LiveData<BaseResultBean<List<FriendApplyBean>>> applyFriendList();

    @POST(YuRuanTalkUrl.FRIEND_LIST)
    LiveData<BaseResultBean<List<FriendBean>>> getFriendOrBlackList(@Body RequestBody body);

    @POST(YuRuanTalkUrl.AGREE_APPLY)
    LiveData<BaseResultBean<String>> agreeFriend(@Body RequestBody body);

    @POST(YuRuanTalkUrl.AGREE_APPLY)
    LiveData<BaseResultBean<String>> ingoreFriend(@Body RequestBody body);

}
