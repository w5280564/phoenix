package com.zhengshuo.phoenix.net;

import androidx.lifecycle.LiveData;
import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.LogTag;
import com.zhengshuo.phoenix.model.BaseResultBean;
import com.fastchat.sdk.utils.JsonUtil;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.util.StringUtil;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicBoolean;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @Description: 数据响应统一处理类
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<R>> {
    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<R> adapt(Call<R> call) {
        return new LiveData<R>() {
            AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(
                            new Callback<R>() {
                                @Override
                                public void onResponse(Call<R> call, Response<R> response) {
                                    LogUtil.d(LogTag.API,"---请求url---" + call.request().url().toString());
                                    R body = response.body();
                                    String path = call.request().url().encodedPath();

                                    // 当没有信息体时通过 http code 判断业务错误
                                    if (body == null && !response.isSuccessful()) {
                                        BaseResultBean result = new BaseResultBean();
                                        result.setCode(result.code);
                                        try {
                                            body = (R) result;
                                        } catch (Exception e) {
                                            // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                        }
                                    } else if (body instanceof BaseResultBean) {
                                        BaseResultBean result = (BaseResultBean) body;
                                        LogUtil.d(LogTag.API,"---返回data---" + JsonUtil.toJson(result));
                                        if (result.code != Constants.REQUEST_SUCCESS_CODE) {
                                            result.setCode(result.code);
                                        }
                                    }
                                    postValue(body);
                                }

                                @Override
                                public void onFailure(Call<R> call, Throwable throwable) {
                                    LogUtil.e(
                                            LogTag.API,
                                            call.request().url().toString()+"接口请求失败 失败原因:"
                                                    + throwable.getMessage());
                                    if (throwable instanceof ConnectException) {
                                        R body = null;
                                        BaseResultBean result = new BaseResultBean();
                                        String errorInfo =throwable.getMessage();
                                        LogUtil.e(LogTag.API,call.request().url().toString()+"接口请求失败 失败原因:"+ errorInfo);
                                        String errorMess = "服务维护中，请稍后再试...";
                                        int failure_code = Constants.REQUEST_FAILURE_SERVER;
                                        if (!StringUtil.isBlank(errorInfo)) {
                                            if (errorInfo.contains("org.apache.http.conn.ConnectTimeoutException")||errorInfo.contains("SocketTimeoutException")||errorInfo.contains("timeout")) {
                                                errorMess = "网络请求超时，请稍后再试";
                                                failure_code = Constants.REQUEST_FAILURE_SERVER;
                                            }else if (errorInfo.contains("No address associated with hostname")||errorInfo.contains("Failed to connect to")) {
                                                errorMess = "无法连接到服务器,请检查网络连接";
                                                failure_code = Constants.REQUEST_FAILURE_INTERNET;
                                            }else if (errorInfo.contains("request failed , reponse's code is : 404")) {
                                                errorMess = "服务维护中，请稍后再试...";
                                                failure_code = Constants.REQUEST_FAILURE_SERVER;
                                            }
                                        }
                                        result.setMsg(errorMess);
                                        result.setCode(failure_code);
                                        try {
                                            body = (R) result;
                                        } catch (Exception e) {
                                            // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                        }
                                        postValue(body);
                                    } else {
                                        postValue(null);
                                    }
                                }
                            });
                }
            }
        };
    }
}
