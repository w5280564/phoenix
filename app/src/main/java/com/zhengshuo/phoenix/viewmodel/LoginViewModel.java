package com.zhengshuo.phoenix.viewmodel;

import android.app.Application;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.model.UserCacheInfoBean;
import com.zhengshuo.phoenix.task.UserTask;
import com.zhengshuo.phoenix.util.SingleSourceLiveData;
import com.zhengshuo.phoenix.util.SingleSourceMapLiveData;

/**
 * @Description: 登录ViewModel
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class LoginViewModel extends AndroidViewModel {
    private SingleSourceLiveData<Resource<String>> loginResult = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<String>> loginOut = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<String>> uploadLogResult = new SingleSourceLiveData<>();
    private MediatorLiveData<Resource> loadingState = new MediatorLiveData<>();


    private SingleSourceMapLiveData<Resource<String>, Resource<String>> sendCodeState;

    private MutableLiveData<Integer> codeCountDown = new MutableLiveData<>();



    private MutableLiveData<UserCacheInfoBean> lastLoginUserCache = new MutableLiveData<>();

    private UserTask userTask;
    private CountDownTimer countDownTimer =
            new CountDownTimer(60 * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int count = Math.round(millisUntilFinished / 1000);
                    codeCountDown.postValue(count);
                }

                @Override
                public void onFinish() {
                    codeCountDown.postValue(0);
                }
            };

    public LoginViewModel(@NonNull Application application) {
        super(application);

        userTask = new UserTask(application);
        loadingState.addSource(loginResult, resource -> loadingState.setValue(resource));

        // 返送验证码请求的结果
        sendCodeState =
                new SingleSourceMapLiveData<>(
                        new Function<Resource<String>, Resource<String>>() {
                            @Override
                            public Resource<String> apply(Resource<String> input) {
                                if (input.status == Status.SUCCESS) {
                                    // 开始计时
                                    startCodeCountDown();
                                }
                                return input;
                            }
                        });



//        UserCacheInfoBean userCache = userTask.getUserCache();
//        if (userCache != null) {
//            lastLoginUserCache.setValue(userCache);
//        }
    }




    public LiveData<Resource<String>> getLoginResult() {
        return loginResult;
    }

    public void registerAndLogin(String phone, String code) {
        loginResult.setSource(userTask.registerAndLogin(phone, code));
    }






    public LiveData<Resource<String>> getUploadLogResult() {
        return uploadLogResult;
    }

    public void uploadLog() {

    }




    /**
     * 验证码接受倒计时
     *
     * @return
     */
    public LiveData<Integer> getCodeCountDown() {
        return codeCountDown;
    }


    /**
     * 最后一次的用户信息
     *
     * @return
     */
    public LiveData<UserCacheInfoBean> getLastLoginUserCache() {
        return lastLoginUserCache;
    }


    /**
     * 验证请求发送结果
     *
     * @return
     */
    public LiveData<Resource<String>> getSendCodeState() {
        return sendCodeState;
    }

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     */
    public void sendCode(String phoneNumber) {
        sendCodeState.setSource(userTask.sendCode(phoneNumber));
    }


    public LiveData<Resource<String>> getLoginOut() {
        return loginOut;
    }

    /**
     * 退出登录
     */
    public void loginOut() {
        loginOut.setSource(userTask.logout());
    }


    private void startCodeCountDown() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    public void stopCodeCountDown() {
        countDownTimer.cancel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
