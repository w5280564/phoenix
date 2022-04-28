package com.zhengshuo.phoenix.net.service;

import androidx.lifecycle.LiveData;

import com.alibaba.fastjson.JSONArray;
import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.GroupInfoBean;
import com.zhengshuo.phoenix.model.GroupListTreeBean;
import com.zhengshuo.phoenix.net.YuRuanTalkUrl;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * @Description: 群相关接口方法
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public interface GroupService {

    @POST(YuRuanTalkUrl.CREATE_GROUP)
    LiveData<BaseResultBean<GroupInfoBean>> createGroup(@Body RequestBody body);


    @POST(YuRuanTalkUrl.MY_GROUP_LIST)
    LiveData<BaseResultBean<GroupListTreeBean>> getMyGroupList();

    @POST(YuRuanTalkUrl.GROUP_USER_LIST)
    LiveData<BaseResultBean<JSONArray>> getGroupUserList(@Body RequestBody body);
}
