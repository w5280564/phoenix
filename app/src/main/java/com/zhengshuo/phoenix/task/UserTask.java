package com.zhengshuo.phoenix.task;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.client.HTClient;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.LoginResultBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.net.HttpClientManager;
import com.zhengshuo.phoenix.net.service.UserService;
import com.zhengshuo.phoenix.net.NetworkOnlyResource;
import com.zhengshuo.phoenix.net.RetrofitUtil;
import java.util.HashMap;
import okhttp3.RequestBody;

/**
 * @Description: 用户相关业务处理
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class UserTask {
    private UserService userService;
    // 存储当前最新一次登录的用户信息

    public UserTask(Context context) {
        userService =
                HttpClientManager.getInstance(context).getClient().createService(UserService.class);
    }




    /**
     * 发送验证码
     *
     * @param phoneNumber
     * @return
     */
    public LiveData<Resource<String>> sendCode(String phoneNumber) {
        return new NetworkOnlyResource<String, BaseResultBean<String>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<String>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("phoneNum", phoneNumber);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return userService.sendCode(body);
            }
        }.asLiveData();
    }




    /** 退出登录 */
    public LiveData<Resource<String>> logout() {
        return new NetworkOnlyResource<String, BaseResultBean<String>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<String>> createCall() {
                return userService.loginOut();
            }
        }.asLiveData();
    }



    /**
     * 用户注册登录
     * @param phone 手机号码
     * @param code 密码
     */
    public LiveData<Resource<String>> registerAndLogin(String phone, String code) {
        MediatorLiveData<Resource<String>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<LoginResultBean>> login =
                new NetworkOnlyResource<LoginResultBean, BaseResultBean<LoginResultBean>>() {
                    @NonNull
                    @Override
                    protected LiveData<BaseResultBean<LoginResultBean>> createCall() {
                        HashMap<String, Object> paramsMap = new HashMap<>();
                        paramsMap.put("phoneNum", phone);
                        paramsMap.put("smsCode", code);
                        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                        return userService.registerAndLogin(body);
                    }
                }.asLiveData();
        result.addSource(
                login,
                new Observer<Resource<LoginResultBean>>() {
                    @Override
                    public void onChanged(Resource<LoginResultBean> loginResultResource) {
                        if (loginResultResource.status == Status.SUCCESS) {
                            result.removeSource(login);
                            LoginResultBean loginResult = loginResultResource.data;
                            if (loginResult != null) {
                                loginIm(loginResult,result);
                            } else {
                                result.setValue(Resource.error(loginResultResource.code, null,loginResultResource.message));
                            }
                        } else if (loginResultResource.status == Status.ERROR) {
                            result.setValue(Resource.error(loginResultResource.code,null, loginResultResource.message));
                        } else {
                            // do nothing
                        }
                    }
                });
        return result;
    }



    private void loginIm(final LoginResultBean loginResult, MediatorLiveData<Resource<String>> result) {
        String userId = loginResult.getImId();
        String password = loginResult.getImPassword();
        //设置本地数据在登录IM之前是因为，IM登录之后回调登录成功之后，会唤起/api/config接口，此时会没有设置token，导致错误
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(loginResult);
        UserManager.get().saveMyUser(jsonObject);
        HTClient.getInstance().login(userId, password, new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                result.postValue(Resource.success(userId,"登录成功"));
            }

            @Override
            public void onError() {
                result.postValue(Resource.error(1002, null,"登录聊天服务器失败"));
            }
        });

    }
}
