package com.zhengshuo.phoenix.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.MmvkManger;
import com.fastchat.sdk.model.HTMessage;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.common.IMAction;

import java.io.File;

/**
 * Created by ouyang on 2017/7/8.
 * 
 */

public class HTMessageUtils {
    /**
     * 创建撤回消息
     *
     * @param htMessage
     * @return
     */
    public static void makeToWithDrawMsg(HTMessage htMessage, String opId, String opNick) {
        JSONObject jsonObject = htMessage.getAttributes();
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put("action", 6001);
        jsonObject.put("opId", opId);
        jsonObject.put("opNick", opNick);
        htMessage.setAttributes(jsonObject);
        HTClient.getInstance().messageManager().saveMessage(htMessage, false);

    }







    /**
     * 复制并转发
     *
     * @param copyType
     * @param localPath
     * @param message1
     */
    public static void showCopySendDialog(Context context, final String copyType, String localPath, final HTMessage message1, String imagePath) {
        if (message1.getType() == HTMessage.Type.TEXT) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(localPath);
            MmvkManger.getInstance().remove("myCopy");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("copyType", copyType);
            jsonObject.put("localPath", localPath);
            jsonObject.put("msgId", message1.getMsgId());
            jsonObject.put("imagePath", imagePath);
            MmvkManger.getInstance().putString("myCopy", jsonObject.toJSONString());
        }
    }





    private static void showForwordDialog(Context context, final String forwordType, final String localPath, final HTMessage message1, String imagePath, String toChatUsername, JSONObject extJSON) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("forwordType", forwordType);
//        jsonObject.put("localPath", localPath);
//        jsonObject.put("msgId", message1.getMsgId());
//        jsonObject.put("imagePath", imagePath);
//        jsonObject.put("toChatUsername", toChatUsername);
//        jsonObject.put("exobj", extJSON.toJSONString());
//        Intent intent = new Intent(context, ForwardSingleActivity.class);
//        intent.putExtra("obj", jsonObject.toJSONString());
//        context.startActivity(intent);todo
    }





    public interface CallBack {
        void error();

        void completed(String localPath);
    }





    /**
     * 更新红包消息
     *
     * @param htMessage
     * @return
     */
    public static void updateRpMessage(HTMessage htMessage, Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.RP_IS_HAS_OPENED).putExtra("message", htMessage));
    }

    /**
     * 下载朋友圈短视频
     *
     * @param context
     * @param videoPath
     * @param callBack
     */
    public static void loadVideoFromService(final Activity context, String videoPath, final CallBack callBack) {
        String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.length());
        String dirFilePath = BaseApplication.getInstance().getVideoPath();
        final File file = new File(dirFilePath + "/" + videoName);
        if (file.exists()) {
            String absolutePath = file.getAbsolutePath();
            callBack.completed(absolutePath);
        } else {
//            CommonUtils.showDialogNumal(context, context.getString(R.string.loading));
//            new OkHttpUtils(context).loadFile(videoPath, file.getAbsolutePath(), new OkHttpUtils.DownloadCallBack() {
//                @Override
//                public void onSuccess() {
//                    context.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            CommonUtils.cencelDialog();
//                            callBack.completed(file.getAbsolutePath());
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    context.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            callBack.error();
//                            CommonUtils.cencelDialog();
//                        }
//                    });
//                }
//            });todo
        }
    }


}
