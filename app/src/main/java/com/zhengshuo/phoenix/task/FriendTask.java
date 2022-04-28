package com.zhengshuo.phoenix.task;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.FriendApplyBean;
import com.zhengshuo.phoenix.model.FriendBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.net.HttpClientManager;
import com.zhengshuo.phoenix.net.service.FriendService;
import com.zhengshuo.phoenix.net.NetworkOnlyResource;
import com.zhengshuo.phoenix.net.RetrofitUtil;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @Description: FriendTask
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class FriendTask {

    private FriendService mFriendService;

    public FriendTask(Context context){
        mFriendService = HttpClientManager.getInstance(context).getClient().createService(FriendService.class);
    }


    /**
     * 搜索好友
     * @param phoneNum 手机号
     * @return
     */
    public LiveData<Resource<SearchFriendInfoBean>> searchFriend(String phoneNum){
        MediatorLiveData<Resource<SearchFriendInfoBean>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<SearchFriendInfoBean>> login =
                new NetworkOnlyResource<SearchFriendInfoBean, BaseResultBean<SearchFriendInfoBean>>() {
                    @NonNull
                    @Override
                    protected LiveData<BaseResultBean<SearchFriendInfoBean>> createCall() {
                        HashMap<String, Object> paramsMap = new HashMap<>();
                        paramsMap.put("search", phoneNum);
                        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                        return mFriendService.searchFriend(body);
                    }
                }.asLiveData();
        result.addSource(
                login,
                new Observer<Resource<SearchFriendInfoBean>>() {
                    @Override
                    public void onChanged(Resource<SearchFriendInfoBean> loginResultResource) {
                        if (loginResultResource.status == Status.SUCCESS) {
                            result.removeSource(login);
                            SearchFriendInfoBean searchResult = loginResultResource.data;
                            if (searchResult != null) {
                                result.postValue(Resource.success(searchResult,loginResultResource.message));
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


    /**
     * 添加好友
     * @param friendUserId 用户id
     * @param remark 备注
     * @return
     */
    public LiveData<Resource<String>> applyFriend(String friendUserId, String remark){
        return new NetworkOnlyResource<String, BaseResultBean<String>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<String>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("friendUserId", friendUserId);
                paramsMap.put("remark", remark);
                paramsMap.put("sourceType", "0");
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return mFriendService.applyFriend(body);
            }
        }.asLiveData();
    }


    /**
     * 获取好友申请列表
     * @return
     */
    public LiveData<Resource<List<FriendApplyBean>>> getFriendApplyList(){
        MediatorLiveData<Resource<List<FriendApplyBean>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<List<FriendApplyBean>>> login =
                new NetworkOnlyResource<List<FriendApplyBean>, BaseResultBean<List<FriendApplyBean>>>() {
                    @NonNull
                    @Override
                    protected LiveData<BaseResultBean<List<FriendApplyBean>>> createCall() {
                        return mFriendService.applyFriendList();
                    }
                }.asLiveData();
        result.addSource(
                login,
                new Observer<Resource<List<FriendApplyBean>>>() {
                    @Override
                    public void onChanged(Resource<List<FriendApplyBean>> resultResource) {
                        if (resultResource.status == Status.SUCCESS) {
                            result.removeSource(login);
                            List<FriendApplyBean> searchResult = resultResource.data;
                            if (searchResult != null) {
                                result.postValue(Resource.success(searchResult,resultResource.message));
                            } else {
                                result.setValue(Resource.error(resultResource.code, null,resultResource.message));
                            }
                        } else if (resultResource.status == Status.ERROR) {
                            result.setValue(Resource.error(resultResource.code,null, resultResource.message));
                        } else {
                            // do nothing
                        }
                    }
                });
        return result;
    }



    /**
     * 接受好友请求
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<String>> agree(String friendId) {
        return new NetworkOnlyResource<String, BaseResultBean<String>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<String>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("friendApplyId", friendId);
                paramsMap.put("type", "1");
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return mFriendService.agreeFriend(body);
            }
        }.asLiveData();
    }

    /**
     * 忽略好友请求
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<String>> ingore(String friendId) {
        return new NetworkOnlyResource<String, BaseResultBean<String>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<String>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("friendApplyId", friendId);
                paramsMap.put("type", "2");
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return mFriendService.ingoreFriend(body);
            }
        }.asLiveData();
    }



    /**
     * 获取好友列表
     * @return
     */
    public LiveData<Resource<List<FriendBean>>> getFriendList(){
        MediatorLiveData<Resource<List<FriendBean>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<List<FriendBean>>> login =
                new NetworkOnlyResource<List<FriendBean>, BaseResultBean<List<FriendBean>>>() {
                    @NonNull
                    @Override
                    protected LiveData<BaseResultBean<List<FriendBean>>> createCall() {
                        HashMap<String, Object> paramsMap = new HashMap<>();
                        paramsMap.put("friendStatus","0");
                        RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                        return mFriendService.getFriendOrBlackList(body);
                    }
                }.asLiveData();
        result.addSource(
                login,
                new Observer<Resource<List<FriendBean>>>() {
                    @Override
                    public void onChanged(Resource<List<FriendBean>> resultResource) {
                        if (resultResource.status == Status.SUCCESS) {
                            result.removeSource(login);
                            List<FriendBean> searchResult = resultResource.data;
                            if (searchResult != null) {
                                result.postValue(Resource.success(searchResult,resultResource.message));
                            } else {
                                result.setValue(Resource.error(resultResource.code, null,resultResource.message));
                            }
                        } else if (resultResource.status == Status.ERROR) {
                            result.setValue(Resource.error(resultResource.code,null, resultResource.message));
                        } else {
                            // do nothing
                        }
                    }
                });
        return result;
    }
}
