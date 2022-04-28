package com.zhengshuo.phoenix.ui.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhengshuo.phoenix.common.GlideEngine;
import com.zhengshuo.phoenix.common.manager.SettingsManager;
import com.zhengshuo.phoenix.ui.video.CameraActivity;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.HTChatManager;
import com.fastchat.sdk.model.CmdMessage;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageTextBody;
import com.fastchat.sdk.model.ReferenceMessage;
import com.fastchat.sdk.utils.JsonUtil;
import com.fastchat.sdk.utils.MessageUtils;
import com.fastchat.sdk.manager.MmvkManger;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.common.HTConstant;
import com.zhengshuo.phoenix.common.manager.ChatFileManager;
import com.zhengshuo.phoenix.common.manager.GroupInfoManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.util.CommonUtils;
import com.zhengshuo.phoenix.util.HTMessageUtils;
import com.zhengshuo.phoenix.util.MsgUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.zhengshuo.phoenix.ui.video.CameraActivity.RESULT_CODE_PERMISSION_REJECT;
import static com.zhengshuo.phoenix.ui.video.CameraActivity.RESULT_CODE_RETURN_PHOTO;
import static com.zhengshuo.phoenix.ui.video.CameraActivity.RESULT_CODE_RETURN_VIDEO;

/**
 * Created by dell on 2017/7/1.
 */


public class ChatPresenter implements ChatContract.Presenter {
    private static final String TAG = "selectPicFromLocal";

    private List<HTMessage> htMessageList = new ArrayList<>();
    private ChatContract.View chatView;
    private String chatTo;
    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CODE_SELECT_FILE = 5;
    private static final int REQUEST_CODE_SELECT_AT_USER = 8;
    private static final int REQUEST_CODE_SELECT_NEAR_IMAGE = 9;
    private static final int REQUEST_CODE_SEND_CARD = 10;
    private int chatType = 1;

    private JSONObject userInfoJson = new JSONObject();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 3000:
                    //获取到最新的20条消息,初始化的时候调用
                    if (msg.obj != null) {
                        List<HTMessage> htMessages = (List<HTMessage>) msg.obj;
                        htMessageList.clear();
                        htMessageList.addAll(htMessages);
                        chatView.initRecyclerView(htMessageList);
//                        if(htMessages.size()<15){
//                            getMoreGroupMsgFromDb();
//                        }
                    }

                    break;
                case 3001:
                    //拉取更多消息
                    if (msg.obj != null) {
                        List<HTMessage> htMessages = (List<HTMessage>) msg.obj;
                        htMessageList.addAll(0, htMessages);
                        chatView.loadMoreMessageRefresh(0, htMessages.size());
                    } else {
                        //chatView.showToast();
                    }
                    break;
                case 3002:
                    //发送一条消息，初始状态-发送中
                    HTMessage htMessage = (HTMessage) msg.obj;
                    if (!htMessageList.contains(htMessage)) {
                        htMessageList.add(htMessage);
                        addToShowDetailsList(htMessage);

                        chatView.insertRecyclerView(htMessageList.size(), 1, 2);
                        Log.d("sendAMessage:", "---->Handler  sendMessage---->" + htMessage.getMsgId());

                    }

                    break;
                case 3003:
                    //发送的消息成功了。状态变更
                    HTMessage htMessage1 = (HTMessage) msg.obj;
                    if (htMessageList.contains(htMessage1)) {

                        chatView.updateRecyclerView(htMessageList.indexOf(htMessage1));
                        CommonUtils.upLoadMessage(htMessage1);
                    }
                    break;
                case 3004:
                    //收到一个新消息
                    HTMessage htMessageReceived = (HTMessage) msg.obj;
                    if (!htMessageList.contains(htMessageReceived)) {
                        htMessageList.add(htMessageReceived);
                        addToShowDetailsList(htMessageReceived);
                        chatView.insertRecyclerView(htMessageList.size(), 1, 1);
                    }

                    break;

                case 3005:
                    //调用sdk发送最终的消息
                    HTMessage htMessageFinal = (HTMessage) msg.obj;
                    ChatPresenter.this.sendMessage(htMessageFinal);
                    break;

                case 3006:
                    chatView.onGroupInfoLoaded();
                    break;
//                case 3007:
//                    //发送的文件不存在
//                    //  CommonUtils.showToastShort(getContext(), R.string.File_does_not_exist);
//                    break;
                case 3008:
                    //发送的文件大于10M
                    chatView.showToast(R.string.size_10M);
                    break;
                case 3009:
                    Bundle bundleImages = msg.getData();
                    String filePath = bundleImages.getString("filePath");
                    String sizeImage = bundleImages.getString("size");
                    sendImageMessage(filePath, sizeImage);
                    break;
                case 4000:
                    //非管理员只能撤回指定时间内的消息
                    //  CommonUtils.showToastShort(getActivity(), R.string.reback_not_more_than_30);
                    break;
                case 4001:

                    CmdMessage cmdMessage = (CmdMessage) msg.obj;
                    sendWithdrawCmd(cmdMessage);


                    break;
                case 4002:
                    HTMessage htMessageWithdraw = (HTMessage) msg.obj;
                    if (htMessageWithdraw != null) {
                        int position = msg.arg1;
                        htMessageList.set(position, htMessageWithdraw);
                        chatView.updateRecyclerView(position);

                    }
                    if (msg.arg2 == 1) {
                        updateMessageInServer(htMessageWithdraw.getMsgId(), htMessageWithdraw);
                    }

                    break;
                case 4003:
                    int resId = msg.arg1;
                    chatView.showToast(resId);

                    break;
                case 4004:
                    Bundle videoBundle = msg.getData();
                    String videoPath = videoBundle.getString("videoPath");
                    String thumbPath = videoBundle.getString("thumbPath");
                    int duration = videoBundle.getInt("duration");
                    String size = videoBundle.getString("size");
                    sendVideoMessage(videoPath, thumbPath, duration, size);
                    break;
                case 4005:
                    getMoreGroupMsgFromDb();
                    break;
                case 4006://删除单条消息
                    HTMessage htMessageDelete = (HTMessage) msg.obj;
                    if (htMessageList.contains(htMessageDelete)) {
                        int index = htMessageList.indexOf(htMessageDelete);
                        htMessageList.remove(htMessageDelete);
                        removeShowDetailsList(htMessageDelete);
                        chatView.deleteItemRecyclerView(index);
                        for (int i = 0; i <htMessageList.size() ; i++) {
                            HTMessage mTempMessage = htMessageList.get(i);
                            HTMessage.Type type = mTempMessage.getType();
                            if (type == HTMessage.Type.TEXT) {
                                HTMessageTextBody htMessageBody = (HTMessageTextBody)mTempMessage.getBody();
                                ReferenceMessage reference = htMessageBody.getReference();
                                if (reference!=null) {
                                    String reference_messageId = reference.getMessageId();
                                    if (htMessageDelete.getMsgId().equals(reference_messageId)) {
                                        mTempMessage.setReferenceId(null);
                                        reference.setMessageId("");
                                        htMessageBody.setReference(reference);
                                        mTempMessage.setBody(htMessageBody);
                                        chatView.updateRecyclerView(i);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HTClient.getInstance().messageManager().saveMessage(mTempMessage, false);
                                            }
                                        }).start();
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 7000:
                    JSONArray noticeList = (JSONArray) msg.obj;
                    if (noticeList != null && noticeList.size() != 0) {
                        JSONObject dataJson = noticeList.getJSONObject(noticeList.size() - 1);
                        String title = dataJson.getString("title");
                        String id = dataJson.getString("noticeId");
                        String content = dataJson.getString("content");
                        String preId = MmvkManger.getInstance().getAsString("group_notice" + BaseApplication.getInstance().getUsername() + chatTo);
                        if (!TextUtils.isEmpty(id) && !id.equals(preId)) {
                            chatView.showNewNoticeDialog(title, content, id);
                        }
                    }
                    break;
//
            }
        }
    };


    public ChatPresenter(ChatContract.View view) {
        chatView = view;
        chatView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void sendZhenMessage() {

    }

    @Override
    public void initData(Bundle bundle) {

        chatType = bundle.getInt("chatType", MessageUtils.CHAT_SINGLE);
        chatTo = bundle.getString("conversationId");
        //附加字段中备注消息发送者信息
        userInfoJson.put("nick", UserManager.get().getMyNick());
        userInfoJson.put("avatar", UserManager.get().getMyAvatar());

        if (chatType == MessageUtils.CHAT_GROUP) {
            //应用每次启动后，群信息只获取一次。设置一个全局静态变量去做标记存取
//            getGroupInfo();
//            getNewNotice();
            getChatHistoryInDb(chatTo);
        } else {
            getChatHistoryInDb(chatTo);
        }


    }


    private void getGroupInfo() {
        if (GroupInfoManager.getInstance().isGroupInfoLoaded(chatTo)) {
            getChatHistoryInDb(chatTo);
            //获取所有群成员
        } else {
            getGroupInfoInServer(chatTo);
            GroupInfoManager.getInstance().getGroupAllMembersFromServer(chatTo,null);
        }
    }

    public void getGroupInfoInServer(String groupId) {

        JSONObject data = new JSONObject();
//            HTMessage  htMessage=HTClient.getInstance().messageManager().getLastMessage(chatTo);
//            if(htMessage!=null){
//                data.put("timestamp", htMessage.getTime());
//            }
//        data.put("groupId", Long.parseLong(groupId));
//        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_INFO, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                String code = jsonObject.getString("code");
//                if ("0".equals(code)) {
//                    JSONObject data = jsonObject.getJSONObject("data");
//
//                    //获取最近的15条消息
//                    JSONArray jsonArray = data.getJSONArray("lastList");
//                    handleMsgData(jsonArray, false);
//                    //下一步需要临时保存群信息，但是最近的20条消息不需要存入临时列表
//                    data.remove("lastList");
//                    GroupInfoManager.getInstance().saveGroupInfoTemp(data);
//                    MmvkManger.getIntance().putJSON(groupId + "_groupInfo_cache", data);
//                    Message message = handler.obtainMessage();
//                    message.what = 3006;
//                    message.sendToTarget();
//                    GroupInfoManager.getInstance().hasGroupInfoLoaded(chatTo);
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });todo

    }


    /**
     * 从DB获取历史消息
     * @param userId
     */
    private void getChatHistoryInDb(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HTMessage> htMessages = HTClient.getInstance().messageManager().getMessageList(userId);
                for (int i = 0; i < htMessages.size(); i++) {
                    HTMessage htMessage = htMessages.get(i);
                    addToShowDetailsList(htMessage);
                    if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                        htMessage.setStatus(HTMessage.Status.FAIL);
                        htMessages.set(i, htMessage);
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                    }
                }


                Message message = handler.obtainMessage();
                message.what = 3000;
                message.obj = htMessages;
                message.sendToTarget();
            }
        }).start();

    }


    @Override
    public void resendMessage(final HTMessage htMessage) {
        int index = htMessageList.indexOf(htMessage);
        htMessageList.remove(htMessage);
        removeShowDetailsList(htMessage);
        chatView.deleteItemRecyclerView(index);
        htMessage.setStatus(HTMessage.Status.CREATE);
        sendMessage(htMessage);
    }


    /**
     * 删除单个消息
     * @param htMessage
     */
    @Override
    public void deleteSingChatMessage(final HTMessage htMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());
                Message message = handler.obtainMessage();
                message.what = 4006;
                message.obj = htMessage;
                message.sendToTarget();
            }
        }).start();
    }


    /**
     * 引用消息
     * @param htMessage
     */
    @Override
    public void referenceMessage(final HTMessage htMessage) {
        chatView.showReferenceView(htMessage);
    }

    /**
     * 切换语音消息的播放模式
     * @param speakerOn 是否是扬声器播放
     */
    @Override
    public void switchVoicePlay(boolean speakerOn) {
        if (speakerOn) {
            ToastUtil.ss("已切换为扬声器播放模式");
            SettingsManager.getInstance().setSettingMsgSpeaker(true);
        }else{
            ToastUtil.ss("已切换为听筒播放模式");
            SettingsManager.getInstance().setSettingMsgSpeaker(false);
        }
    }


    //撤回一条消息
    @Override
    public void withdrawMessage(final HTMessage htMessage, final int positionTemp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CmdMessage cmdMessage = new CmdMessage();
                cmdMessage.setTo(chatTo);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", 6000);
                jsonObject.put("msgId", htMessage.getMsgId());
                jsonObject.put("opId", UserManager.get().getMyImId());
                jsonObject.put("opNick", UserManager.get().getMyNick());

                cmdMessage.setBody(jsonObject.toString());
                if (chatType == MessageUtils.CHAT_GROUP) {
                    cmdMessage.setChatType(ChatType.groupChat);
                }
                Message message = handler.obtainMessage();
                message.what = 4001;
                message.obj = cmdMessage;
                message.sendToTarget();//发送消息
                int position = htMessageList.indexOf(htMessage);
                HTMessageUtils.makeToWithDrawMsg(htMessage, UserManager.get().getMyImId(), UserManager.get().getMyNick());
                removeShowDetailsList(htMessage);
                Message handlerMessage = handler.obtainMessage();
                handlerMessage.obj = htMessage;
                handlerMessage.what = 4002;
                handlerMessage.arg1 = position;
                handlerMessage.arg2 = 1;
                handlerMessage.sendToTarget();//通知更新本地消息
            }
        }).start();


    }

    private void sendWithdrawCmd(CmdMessage cmdMessage) {
        HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
            }

            @Override
            public void onFailure() {

            }
        });
    }

    /**
     * 收到撤回通知
     */
    @Override
    public void onMessageWithdraw(HTMessage htMessageWD) {
        if (htMessageWD.getUsername().equals(chatTo)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < htMessageList.size(); i++) {
                        HTMessage htMessage = htMessageList.get(i);
                        if (htMessage.getMsgId().equals(htMessageWD.getMsgId())) {
                            removeShowDetailsList(htMessage);
                            Message message = handler.obtainMessage();
                            message.obj = htMessageWD;
                            message.what = 4002;
                            message.arg1 = i;
                            message.arg2 = 0;
                            message.sendToTarget();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 更新撤回消息服务器端
     *
     * @param msgId
     */
    private void updateMessageInServer(final String msgId, final HTMessage message) {

        JSONObject data = new JSONObject();
        data.put("messageId", msgId);
        try {
            data.put("content", URLEncoder.encode(message.toXmppMessageBody(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        ApiUtis.getInstance().postJSON(data, Constant.URL_MESSAGE_UPDATE, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });todo


    }

    @Override
    public void onNewMessage(final HTMessage htMessage) {
        if (htMessage.getUsername().equals(chatTo)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = handler.obtainMessage();
                    message.obj = htMessage;
                    message.what = 3004;
                    message.sendToTarget();
                    HTClient.getInstance().conversationManager().markAllMessageRead(chatTo);
                }
            }).start();
        }
    }


    /**
     * 选择个人名片
     */
    @Override
    public void startCardSend(Activity activity) {
//        activity.startActivityForResult(new Intent(activity, CheckCardActivity.class), REQUEST_CODE_SEND_CARD);//todo
    }
    private Map<String ,String > atUserMap=new HashMap<>();

    @Override
    public void setAtUser(String nick, String userId) {
        Log.d("nick---5",nick);
        Log.d("nick---6",userId);
        atUserMap.put(nick,userId);
    }

    @Override
    public boolean isHasAt(String userId) {
        return atUserMap.containsValue(userId);
    }
    @Override
    public boolean isHasAtNick(String nick) {
        Log.d("nick---9",nick);
        return atUserMap.containsKey(nick);
    }
    @Override
    public void startChooseAtUser() {
//        chatView.getBaseActivity(). startActivityForResult(new Intent(chatView.getBaseContext(), PickAtUserActivity.class).
//                putExtra("groupId", chatTo), REQUEST_CODE_SELECT_AT_USER);//todo
    }

    @Override
    public void deleteAtUser(String nick) {
        atUserMap.remove(nick);
    }

    @Override
    public void refreshHistory() {
        JSONObject data = new JSONObject();
        data.put("referId", chatTo);
        data.put("referType", chatType);


//        ApiUtis.getInstance().postJSON(data, Constant.URL_CHAT_HISTORY, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                String code = jsonObject.getString("code");
//                if ("0".equals(code)) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    Log.d("handleList--->refresh", jsonArray.toJSONString());
//                    handleMsgData(jsonArray, false);
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });//todo


    }

    @Override
    public void onMessageForward(HTMessage htMessage) {
        if (htMessage.getUsername().equals(chatTo)) {
            htMessageList.add(htMessage);
        }
    }

    @Override
    public void onMessageClear() {
        //单聊只需要清除本地聊天记录。群聊暂不清理（如果清理，需要设置服务器端取消息的起始时间戳，可以通过更新进群时间实现）
        htMessageList.clear();
        chatView.notifyClear();
    }



    @Override
    public void loadMoreMessages() {
        if (htMessageList == null || htMessageList.size() == 0) {
            return;
        }
        final HTMessage htMessage = htMessageList.get(0);

//        if (chatType == MessageUtils.CHAT_GROUP) {
//            getMessageListFromServer(chatTo, htMessage.getTime());
//            return;
//        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, htMessage.getTime(), 15);
                if (htMessages.size() == 0) {
                    Message message = handler.obtainMessage();
                    message.what = 4003;
                    message.arg1 = R.string.no_more_msg;
                    message.sendToTarget();
                } else {
                    for (int i = 0; i < htMessages.size(); i++) {
                        HTMessage htMessage = htMessages.get(i);
                        addToShowDetailsList(htMessage);
                        if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                            htMessage.setStatus(HTMessage.Status.FAIL);
                            htMessages.set(i, htMessage);
                            HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.what = 3001;
                    message.obj = htMessages;
                    message.sendToTarget();

                }
            }
        }).start();

    }

    private void getMoreGroupMsgFromDb() {
        if (htMessageList == null || htMessageList.size() == 0) {
            //此时历史记录全为空
            getChatHistoryInDb(chatTo);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final HTMessage htMessageLast = htMessageList.get(0);
                    List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, htMessageLast.getTime(), 15);
                    if (htMessages.size() == 0) {
                        Message message = handler.obtainMessage();
                        message.what = 4003;
                        message.arg1 = R.string.no_more_msg;
                        message.sendToTarget();
                    } else {
                        for (int i = 0; i < htMessages.size(); i++) {
                            HTMessage htMessage = htMessages.get(i);
                            addToShowDetailsList(htMessage);
                            if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                                htMessage.setStatus(HTMessage.Status.FAIL);
                                htMessages.set(i, htMessage);
                                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                            }
                        }
                        Message message = handler.obtainMessage();
                        message.what = 3001;
                        message.obj = htMessages;
                        message.sendToTarget();

                    }
                }
            }).start();


        }
    }


    private void sendVideoMessage(final String videoPath, String thumbPath, final int duration, String size) {
        File file = new File(videoPath);
        if (TextUtils.isEmpty(videoPath) || TextUtils.isEmpty(thumbPath) || !file.exists() || !new File(thumbPath).exists()) {
            Message message = handler.obtainMessage();
            message.what = 4003;
            message.arg1 = R.string.video_path_error;
            message.sendToTarget();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            Message message = handler.obtainMessage();
            message.what = 4003;
            message.arg1 = R.string.size_10M;
            message.sendToTarget();
            return;
        }

        HTMessage htMessage = HTMessage.createVideoSendMessage(chatTo, videoPath, thumbPath, duration, size);
        sendMessage(htMessage);
        ChatFileManager.get().setLocalPath(htMessage.getMsgId(), videoPath, htMessage.getType());
    }


    @Override
    public void sendTextMessage(final String content, ReferenceMessage mReferenceMessageObj) {
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, content,mReferenceMessageObj);
        if(chatType==MessageUtils.CHAT_GROUP&&content.contains("@")&&content.contains(" ")){
            //String nick=content.substring(content.indexOf("@"),content.indexOf(" "));
            String[] atUsers=content.split("@");
            String atUserId="";
            for(int i=0;i<atUsers.length;i++){
                String userString=atUsers[i];
                 if(userString.endsWith(" ")){
                    userString= userString.substring(0,userString.length()-1);
                     String userId=atUserMap.get(userString);
                    if(userId!=null){
                        atUserId=atUserId+userId+",";
                    }
                }else if(userString.contains(" ")) {
                     userString=userString.substring(0,userString.indexOf(" "));
                     String userId=atUserMap.get(userString);
                     if(userId!=null){
                         atUserId=atUserId+userId+",";
                     }
                 }
            }
            if(!TextUtils.isEmpty(atUserId)){
                atUserId=atUserId.substring(0,atUserId.length()-1);
                Log.d("ext---",atUserId);
                htMessage.setAttribute("atUser",atUserId);
             }

        }

        sendMessage(htMessage);
        chatView.hideReferenceView();
    }


    @Override
    public void selectPicFromCamera(Activity activity) {
        activity.startActivityForResult(new Intent(activity, CameraActivity.class).putExtra("onlyPhotograph", false), REQUEST_CODE_CAMERA);//请自定义最后一个参数：常量
    }

    @Override
    public void selectPicFromLocal(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(SelectMimeType.ofAll())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(9)
                .isGif(true)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> selectList) {
                        List<String> images = new ArrayList<>();
                        List<String> videos = new ArrayList<>();
                        List<LocalMedia> videosLocalMedia = new ArrayList<>();
                        for (LocalMedia media : selectList) {
                            String realPath = media.getRealPath();
                            if (PictureMimeType.isHasImage(media.getMimeType())) {
                                images.add(realPath);
                            } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                videos.add(realPath);
                                videosLocalMedia.add(media);
                            }
                            Log.i(TAG, "文件名: " + media.getFileName());
                            Log.i(TAG, "是否压缩:" + media.isCompressed());
                            Log.i(TAG, "压缩:" + media.getCompressPath());
                            Log.i(TAG, "初始路径:" + media.getPath());
                            Log.i(TAG, "绝对路径:" + media.getRealPath());
                            Log.i(TAG, "是否裁剪:" + media.isCut());
                            Log.i(TAG, "裁剪:" + media.getCutPath());
                            Log.i(TAG, "是否开启原图:" + media.isOriginal());
                            Log.i(TAG, "原图路径:" + media.getOriginalPath());
                            Log.i(TAG, "沙盒路径:" + media.getSandboxPath());
                            Log.i(TAG, "水印路径:" + media.getWatermarkPath());
                            Log.i(TAG, "视频缩略图:" + media.getVideoThumbnailPath());
                            Log.i(TAG, "原始宽高: " + media.getWidth() + "x" + media.getHeight());
                            Log.i(TAG, "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
                            Log.i(TAG, "文件大小: " + media.getSize());
                        }
                        LogUtil.d("selectPicFromLocal", JsonUtil.arrayToJson(images));
                        uploadLocalFiles(images);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i <videos.size() ; i++) {
                                    String url = videos.get(i);
                                    LocalMedia mVideosLocalMedia = videosLocalMedia.get(i);
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    try {
                                        mediaPlayer.setDataSource(url);
                                        mediaPlayer.prepare();
                                        int duration = mediaPlayer.getDuration() / 1000;
                                        mediaPlayer.release();
                                        File file = new File(BaseApplication.getInstance().getVideoPath(), "th_video" + System.currentTimeMillis() + ".png");
                                        FileOutputStream fos = new FileOutputStream(file);
                                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                                        ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                        fos.close();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("videoPath", url);
                                        bundle.putString("thumbPath", file.getAbsolutePath());
                                        bundle.putInt("duration", duration);
                                        bundle.putString("size", mVideosLocalMedia.getWidth() + "," + mVideosLocalMedia.getHeight());
                                        Message message = handler.obtainMessage();
                                        message.what = 4004;
                                        message.setData(bundle);
                                        message.sendToTarget();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data, final Context context) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_CODE_RETURN_PHOTO) {//拍照
                //照片路径
                String photoPath = data.getStringExtra("path");
                List<String> list = new ArrayList<>();
                list.add(photoPath);
                uploadLocalFiles(list);
            } else if (resultCode == RESULT_CODE_RETURN_VIDEO) {//摄像
                //视频第一帧图片路径
                String firstVideoPicture = data.getStringExtra("path");
                //视频路径，该路径为已压缩过的视频路径
                String videoPath = data.getStringExtra("videoPath");

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(videoPath);
                    mediaPlayer.prepare();
                    int duration = mediaPlayer.getDuration() / 1000;
                    int height = mediaPlayer.getVideoHeight();
                    int width = mediaPlayer.getVideoWidth();

                    mediaPlayer.release();

                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", videoPath);
                    bundle.putString("thumbPath", firstVideoPicture);
                    bundle.putInt("duration", duration);
                    bundle.putString("size", width + "," + height);
                    Message message = handler.obtainMessage();
                    message.what = 4004;
                    message.setData(bundle);
                    message.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == RESULT_CODE_PERMISSION_REJECT) {
                Message message = handler.obtainMessage();
                message.what = 4003;
                message.arg1 = R.string.no_permission;
                message.sendToTarget();
            }
        }


        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
//                        String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
//                        Uri uri = Uri.parse(path);
//                        if (uri != null) {
//                            //  sendFileByUri(uri, path);
//                        }todo
                    }
                    break;
                case REQUEST_CODE_MAP:
//                    double latitude = data.getDoubleExtra("latitude", 0);
//                    double longitude = data.getDoubleExtra("longitude", 0);
//                    String locationAddress = data.getStringExtra("address");
//                    String thumbailPath = data.getStringExtra("thumbnailPath");
//                    if (!TextUtils.isEmpty(locationAddress) && new File(thumbailPath).exists()) {
//                        sendLocationMessage(latitude, longitude, locationAddress, thumbailPath);
//                    } else {
//                        // CommonUtils.showToastShort(getContext(), R.string.unable_to_get_loaction);
//                    }
                    break;
                case REQUEST_CODE_SELECT_NEAR_IMAGE:
                    if (data != null) {
                        String path = data.getStringExtra("path");
                        List<String> list = new ArrayList<>();
                        list.add(path);
                        uploadLocalFiles(list);
                    }
                    break;
                case REQUEST_CODE_SEND_CARD:
                    if (data != null) {
                        String user = data.getStringExtra("user");
                        sendCardMessage(user);
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {

                        String userId = data.getStringExtra(HTConstant.JSON_KEY_USERID);

                        if(isHasAt(userId)){
                            return;
                        }
                        String realNick=UserManager.get().getUserRealNick(userId);
                        if(userId.equals("全体成员")){
                            realNick="全体成员";
                            userId="all";
                        }

                        chatView.setAtUserStyle(realNick,true);
                        setAtUser(realNick,userId);

                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void sendCardMessage(final String user) {


        if (TextUtils.isEmpty(user)) {
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(user);
            JSONObject extJSON = new JSONObject();
            extJSON.put("action", 10007);
            extJSON.put("cardUserId", jsonObject.getString("userId"));
            extJSON.put("cardUserNick", UserManager.get().getUserRealNick(jsonObject.getString("userId")));
            extJSON.put("cardUserAvatar", jsonObject.getString(HTConstant.JSON_KEY_AVATAR));
            HTMessage message = HTMessage.createTextSendMessage(chatTo, chatView.getBaseContext().getString(R.string.self_card),null);
            message.setAttributes(extJSON);
            sendMessage(message);
        } catch (Exception e) {
        }


    }
    private long preTime=0;
    private void sendMessage(final HTMessage htMessage) {
        long currentTime=System.currentTimeMillis();
//        if((currentTime-preTime)/1000 <1){
//            chatView.showToast(R.string.send_too_much);
//            return;
//        }

        JSONObject ext = htMessage.getAttributes();

        if (ext == null) {
            ext = new JSONObject();
        }
        Log.d("ext--->",ext.toJSONString());
        ext.putAll(userInfoJson);
        htMessage.setAttributes(ext);
        if (chatType == MessageUtils.CHAT_GROUP) {
            htMessage.setChatType(ChatType.groupChat);
        }else {
            //强制好友可发消息
//           if( !UserManager.get().getFriends().contains(chatTo)){
//             LocalBroadcastManager.getInstance(chatView.getBaseContext()).sendBroadcast(new Intent(IMAction.CMD_DELETE_FRIEND).putExtra("userId", chatTo));
//               return;
//           }

        }

        if (GroupInfoManager.getInstance().isGroupSilent(chatTo) && !GroupInfoManager.getInstance().isManager(chatTo)) {
            chatView.showToast(R.string.has_no_talk);
            return;
        }


        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
                Log.d("sendAMessage:", "---->APP  sendMessage---->" + htMessage.getMsgId());
                Message message = handler.obtainMessage();
                message.what = 3002;
                message.obj = htMessage;
                handler.sendMessage(message);
            }

            @Override
            public void onSuccess(long timeStamp) {
                htMessage.setStatus(HTMessage.Status.SUCCESS);
                htMessage.setTime(timeStamp);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);

                Message message = handler.obtainMessage();
                message.what = 3003;
                message.obj = htMessage;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure() {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = handler.obtainMessage();
                message.what = 3003;
                message.obj = htMessage;
                handler.sendMessage(message);

            }

        });

        //清除@标记
        atUserMap.clear();
        preTime=System.currentTimeMillis();
    }

    private void sendImageMessage(final String imagePath, String size) {

        HTMessage htMessage = HTMessage.createImageSendMessage(chatTo, imagePath, size);
        sendMessage(htMessage);

    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }


    /**
     * 上传多文件
     * @param pathList 传入的为图片原始路径
     */
    @SuppressLint("CheckResult")
    private void uploadLocalFiles(final List<String> pathList) {
        for (int i = 0; i < pathList.size(); i++) {
            String filePath = pathList.get(i);
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Bitmap bitmap = rotateBitmap(bmp, readPictureDegree(filePath));
            String size = bitmap.getWidth() + "," + bitmap.getHeight();
            Bundle bundle = new Bundle();
            bundle.putString("size", size);
            bundle.putString("filePath", filePath);
            Message message = handler.obtainMessage();
            message.what = 3009;
            message.setData(bundle);
            handler.sendMessageDelayed(message, i * 1000);
        }
    }


    @Override
    public void sendVoiceMessage(final String filePath, final int length) {
        HTMessage htMessage = HTMessage.createVoiceSendMessage(chatTo, filePath, length);
        sendMessage(htMessage);
        ChatFileManager.get().setLocalPath(htMessage.getMsgId(), filePath, htMessage.getType());
    }



    @SuppressLint("CheckResult")
    private void getMessageListFromServer(String chatTo, long timestamp) {
        JSONObject data = new JSONObject();
        data.put("referId", chatTo);
        data.put("referType", chatType);
        if(timestamp!=0){
            data.put("time", timestamp);
        }

//        ApiUtis.getInstance().postJSON(data, Constant.URL_CHAT_HISTORY, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                String code = jsonObject.getString("code");
//                if ("0".equals(code)) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    Log.d("handleList--->2", jsonArray.toJSONString());
//                    handleMsgData(jsonArray, true);
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });todo


    }


    private void handleMsgData(JSONArray jsonArray, boolean isMore) {
        //是否是加载更多
        List<HTMessage> messages = new ArrayList<>();
        if (jsonArray != null) {

            if (jsonArray.size() == 0) {
                Message message = handler.obtainMessage();
                message.what = 4005;
                message.sendToTarget();
            } else {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject msgJSON = jsonArray.getJSONObject(i);
                    String base64String = msgJSON.getString("content");
                    String userId = msgJSON.getString("userId");
                    String nick = msgJSON.getString("nick");
                    String avatar = msgJSON.getString("avatar");
                    long time = msgJSON.getLong("time");
                    //保存用户信息
                    UserManager.get().saveUserNickAvatar(userId, nick, avatar);
                    //转化成消息列表
                    HTMessage htMessage = MsgUtils.getInstance().stringToMessage(base64String, time);
                    if (htMessage != null) {
                        messages.add(htMessage);
                        addToShowDetailsList(htMessage);
                        //消息存库，应用未关闭的情况下次进入该界面，直接取存库的消息
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);

                    }

                }
                Message message = handler.obtainMessage();
                message.what = 3000;
                if (isMore) {
                    message.what = 3001;

                }
                message.obj = messages;
                message.sendToTarget();

            }


        }
    }

    private void addToShowDetailsList(HTMessage htMessage) {
        if (htMessage.getType() == HTMessage.Type.VIDEO || htMessage.getType() == HTMessage.Type.IMAGE) {
            ChatFileManager.get().addImageOrVideoMessage(htMessage);
        }
    }

    private void removeShowDetailsList(HTMessage htMessage) {
        if (htMessage.getType() == HTMessage.Type.VIDEO || htMessage.getType() == HTMessage.Type.IMAGE) {
            ChatFileManager.get().removeImageOrVideoMessage(htMessage);
        }
    }



    private void getNewNotice() {

        JSONObject body = new JSONObject();
        body.put("groupId", chatTo);

//        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_NOTICE_LIST, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//
//                String code = jsonObject.getString("code");
//                if ("0".equals(code)) {
//                    JSONArray data = jsonObject.getJSONArray("data");
//                    Message message = handler.obtainMessage();
//                    message.obj = data;
//                    message.what = 7000;
//                    message.sendToTarget();
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });TODO


    }
}
