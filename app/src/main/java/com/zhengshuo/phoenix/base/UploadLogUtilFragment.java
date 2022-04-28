package com.zhengshuo.phoenix.base;

import android.Manifest;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.utils.http.HttpSender;
import com.fastchat.sdk.utils.http.MyOnHttpResListener;
import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.manager.PermissionManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.net.YuRuanTalkUrl;
import com.zhengshuo.phoenix.util.ToastUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * 上传文件的基类
 *
 */

public abstract class UploadLogUtilFragment extends BaseFragment{
    private static final String ABSOLUTE_PATH = "/sdcard/Android/data/com.fastchat.app/files/yuruan_log/";
    private static final String LOGCAT_NAME = ".log";
    private final String LOG_NAME_FORMAT = "user_%s";
    private ActivityResultLauncher<String[]> launcher;


    @Override
    protected void initView(View mRootView) {
        super.initView(mRootView);
        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (Objects.requireNonNull(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)).equals(true)&& Objects.requireNonNull(result.get(Manifest.permission.READ_EXTERNAL_STORAGE)).equals(true)) {
                        uploadFile();
                    } else {
                        //有权限没有获取到的动作
                    }
                });
    }

    /**
     * 检查读写权限
     */
    protected void uploadLog() {
        boolean result = PermissionManager.checkPermission(mActivity, Constants.PERMS_WRITE_READ);
        if (result) {
            uploadFile();
        }else{
            launcher.launch(Constants.PERMS_WRITE_READ);
        }
    }









    private void uploadFile(){
        Log.e("uploadFile","文件名称:"+ABSOLUTE_PATH+getFileName());
        File uploadFile = new File(ABSOLUTE_PATH+getFileName());
        if (!uploadFile.exists()) {
            ToastUtil.sl("日志不存在");
            return;
        }else{
            Log.e("uploadFile","存在>>>文件大小"+uploadFile.getAbsoluteFile().length());
            Log.e("uploadFile","存在>>>文件名称"+uploadFile.getName());
        }
        showLoadingDialog("上传中...");
        HashMap<String, String> baseMap = new HashMap<>();
        baseMap.put("uid", UserManager.get().getMyUserId());
        baseMap.put("meid",UserManager.get().getDeviceId());
        HttpSender sender = new HttpSender(YuRuanTalkUrl.UPLOAD_RECORD, "上传日志", baseMap,
                new MyOnHttpResListener() {
                    @Override
                    public void onComplete(JSONObject json_root, int status, String description) {
                        if (status == SDKConstant.REQUEST_SUCCESS_CODE) {
                            dismissLoadingDialog(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.sl("上传成功");
                                        }
                                    });
                        }else{
                            dismissLoadingDialog(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.ss(description);
                                        }
                                    });
                        }
                    }
                });
        sender.sendPostFile(uploadFile);
    }
    public String getFileName() {
        String userName = HTPreferenceManager.getInstance().getUser().getUsername();
        return String.format(LOG_NAME_FORMAT, userName) + LOGCAT_NAME;
    }

}
