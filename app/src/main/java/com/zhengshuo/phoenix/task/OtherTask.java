package com.zhengshuo.phoenix.task;

import android.content.Context;

import com.zhengshuo.phoenix.net.HttpClientManager;
import com.zhengshuo.phoenix.net.service.OtherService;

/**
 * @Description: OtherTask
 * @Author: ouyang
 * @CreateDate: 2022/3/31 0031
 */
public class OtherTask {

    private OtherService otherService;

    public OtherTask(Context context) {
        otherService =
                HttpClientManager.getInstance(context).getClient().createService(OtherService.class);
    }


//    public LiveData<Resource<BaseResultBean>> setPortrait(Uri imageUri) {
//        MediatorLiveData<Resource<BaseResultBean>> result = new MediatorLiveData<>();
//        result.setValue(Resource.loading(null));
//        LiveData<Resource<String>> uploadResource = fileManager.uploadImage(imageUri);
//        result.addSource(
//                uploadResource,
//                new Observer<Resource<String>>() {
//                    @Override
//                    public void onChanged(Resource<String> resource) {
//                        if (resource.status != Status.LOADING) {
//                            result.removeSource(uploadResource);
//                        }
//
//                        if (resource.status == Status.ERROR) {
//                            result.setValue(Resource.error(resource.code, null));
//                            return;
//                        }
//
//                        if (resource.status == Status.SUCCESS) {
//                            LiveData<Resource<BaseResultBean>> setPortrait = setPortrait(resource.data);
//                            result.addSource(
//                                    setPortrait,
//                                    new Observer<Resource<BaseResultBean>>() {
//                                        @Override
//                                        public void onChanged(Resource<BaseResultBean> resultResource) {
//
//                                            if (resultResource.status != Status.LOADING) {
//                                                result.removeSource(setPortrait);
//                                            }
//
//                                            if (resultResource.status == Status.ERROR) {
//                                                result.setValue(
//                                                        Resource.error(resultResource.code, null));
//                                                return;
//                                            }
//                                            if (resultResource.status == Status.SUCCESS) {
//                                                result.setValue(resultResource);
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                });
//
//        return result;
//    }
}
