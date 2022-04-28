package com.zhengshuo.phoenix.net;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.utils.http.HttpSender;
import com.fastchat.sdk.utils.http.MyOnHttpResListener;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class FileManager {
    private Context context;

    public FileManager(Context context) {
        this.context = context.getApplicationContext();
        RetrofitClient client = HttpClientManager.getInstance(context).getClient();
    }







    /**
     *
     * @param fileUri
     * @param uploadToken
     * @return
     */
    private LiveData<Resource<String>> uploadFileByHuaWei(Uri fileUri, String uploadToken) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        File uploadFile = new File(fileUri.getPath());
        if (!uploadFile.exists()) {
            uploadFile = new File(getRealPathFromUri(fileUri));
        }
        HashMap<String, String> baseMap = new HashMap<>();
        baseMap.put("uid", UserManager.get().getMyUserId());
        baseMap.put("meid",getUUID());
        HttpSender sender = new HttpSender(YuRuanTalkUrl.UPLOAD_RECORD, "上传日志", baseMap,
                new MyOnHttpResListener() {
                    @Override
                    public void onComplete(JSONObject json_root, int status, String description) {
                        if (status == SDKConstant.REQUEST_SUCCESS_CODE) {

                        }
                    }
                });
        sender.sendPostFile(uploadFile);

        return result;
    }


    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 获取本地文件真实 uri
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
