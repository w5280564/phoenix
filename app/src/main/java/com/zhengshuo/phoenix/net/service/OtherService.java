package com.zhengshuo.phoenix.net.service;

import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.net.YuRuanTalkUrl;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @Description: OtherTask
 * @Author: ouyang
 * @CreateDate: 2022/3/31 0031
 */
public interface OtherService {


    @POST(YuRuanTalkUrl.FRIEND_SEARCH)
    LiveData<BaseResultBean<SearchFriendInfoBean>> searchFriend(@Body RequestBody body);
}
