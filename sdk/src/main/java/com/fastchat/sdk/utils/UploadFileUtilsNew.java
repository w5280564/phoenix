package com.fastchat.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.utils.http.HttpSender;
import com.fastchat.sdk.utils.http.MyOnHttpResListener;
import java.io.File;
import java.util.HashMap;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class UploadFileUtilsNew {

    private File file;



    public UploadFileUtilsNew(File file) {
        this.file = file;
    }

    // 从本地文件上传，使用非阻塞的异步接口
    public void asyncUploadFile(final UploadCallBack uploadCallBack) {
        HashMap<String, String> baseMap = new HashMap<>();
        HttpSender sender = new HttpSender(SDKConstant.URL_UPLOAD_IMAGE, "聊天图片上传", baseMap,
                new MyOnHttpResListener() {
                    @Override
                    public void onComplete(JSONObject json_root, int status, String description) {
                        if (status == SDKConstant.REQUEST_SUCCESS_CODE) {
                            String remote_url = json_root.getString("data");
                            uploadCallBack.onSuccess(remote_url);
                        }else{
                            uploadCallBack.onFailure(description);
                        }
                    }
                });
        sender.sendPostFile(file);
    }






  public interface UploadCallBack {
        void onSuccess(String remotePath);
        void onFailure(String description);
    }

}
