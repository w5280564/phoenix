package com.zhengshuo.phoenix.net;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.LogTag;
import com.zhengshuo.phoenix.common.manager.ThreadManager;
import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.util.LogUtil;

public abstract class NetworkOnlyResource<ResultType, RequestType> {
    private final ThreadManager threadManager;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkOnlyResource() {
        this.threadManager = ThreadManager.getInstance();
        if (threadManager.isInMainThread()) {
            init();
        } else {
            threadManager.runOnUIThread(() -> init());
        }
    }

    private void init() {
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        LiveData<RequestType> apiResponse = createCall();
        result.addSource(
                apiResponse,
                response -> {
                    result.removeSource(apiResponse);
                    if (response != null) {
                        if (response instanceof BaseResultBean) {
                            int code = ((BaseResultBean) response).code;
                            String message = ((BaseResultBean) response).msg;
                            if (code != Constants.REQUEST_SUCCESS_CODE) {
                                result.setValue(Resource.error(code, null,message));
                                return;
                            } else {
                                // do nothing
                            }
                        }
                        threadManager.runOnWorkThread(
                                () -> {
                                    ResultType resultType = transformRequestType(response); // 自定义的
                                    if (resultType == null) {
                                        resultType = transformDefault(response); // 默认
                                    }
                                    try {
                                        saveCallResult(resultType);
                                    } catch (Exception e) {
                                        LogUtil.d(LogTag.DB, "saveCallResult failed:" + e.toString());
                                    }
                                    result.postValue(Resource.success(resultType,""));
                                });
                    } else {
                        result.setValue(Resource.error(Constants.REQUEST_FAILURE_INTERNET, null, "服务维护中"));
                    }
                });
    }

    /**
     * 重写此方法完成请求类型和响应类型转换 如果是请求结果是 BaseResultBean<ResultType> 类型则不用重写
     *
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType transformRequestType(RequestType response) {
        return null;
    }

    @WorkerThread
    private ResultType transformDefault(RequestType response) {
        if (response instanceof BaseResultBean) {
            Object result = ((BaseResultBean) response).getData();
            if (result != null) {
                try {
                    return (ResultType) result;
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected void saveCallResult(@NonNull ResultType item) {}

    @NonNull
    @MainThread
    protected abstract LiveData<RequestType> createCall();
}
