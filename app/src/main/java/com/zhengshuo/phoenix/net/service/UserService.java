package com.zhengshuo.phoenix.net.service;

import androidx.lifecycle.LiveData;

import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.LoginResultBean;
import com.zhengshuo.phoenix.net.YuRuanTalkUrl;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * @Description: 用户相关接口方法
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public interface UserService {

    @POST(YuRuanTalkUrl.SEND_CODE)
    LiveData<BaseResultBean<String>> sendCode(@Body RequestBody body);



    @POST(YuRuanTalkUrl.REGISTER_AND_LOGIN)
    LiveData<BaseResultBean<LoginResultBean>> registerAndLogin(@Body RequestBody body);

    @POST(YuRuanTalkUrl.LOGIN_OUT)
    LiveData<BaseResultBean<String>> loginOut();
}
