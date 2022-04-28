package com.zhengshuo.phoenix.common.manager;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.manager.MmvkManger;



/**
 * Created by ouyang on 2019/7/26.
 * 
 */
public class SettingsManager {

    private static SettingsManager systemDataManager;

    public static SettingsManager getInstance() {
        if (systemDataManager == null) {
            systemDataManager = new SettingsManager();

        }
        return systemDataManager;

    }

    public void setShareJSON(JSONObject json){
        MmvkManger.getInstance().putJSON("share_JSON",json);
    }
    public JSONObject getShareJSON(){

        return MmvkManger.getInstance().getJSON("share_JSON");
    }

    public void savaDeviceId(String deviceId){
        MmvkManger.getInstance().putString("deviceId",deviceId);
    }

    public String getDeviceId(){
     return    MmvkManger.getInstance().getAsString("deviceId");
    }
    public void savaKeyboardHeight(int keyboardHeight){
        MmvkManger.getInstance().putInt("keyboardHeight",keyboardHeight);
    }
    public int getKeyboardHeight2(){
       return MmvkManger.getInstance().getInt("keyboardHeight2",0);
    }
    public int getKeyboardHeight(){
        return MmvkManger.getInstance().getInt("keyboardHeight",0);
    }
    public void savaKeyboardHeight2(int keyboardHeight){
        MmvkManger.getInstance().putInt("keyboardHeight2",keyboardHeight);
    }

    public boolean getSettingMsgNotification(){
        return MmvkManger.getInstance().getBoolean("MsgNotification",true);
    }

    public void setSettingMsgNotification(boolean isNotify){
        MmvkManger.getInstance().putBoolean("MsgNotification",isNotify);
    }

    public boolean getSettingMsgSound(){
        return MmvkManger.getInstance().getBoolean("MsgSound",true);

    }

    public boolean getSettingMsgVibrate(){
        return MmvkManger.getInstance().getBoolean("MsgVibrate",true);

    }

    public void setSettingMsgSound(boolean isNotify){
        MmvkManger.getInstance().putBoolean("MsgSound",isNotify);
    }

    public void setSettingMsgVibrate(boolean isNotify){
        MmvkManger.getInstance().putBoolean("MsgVibrate",isNotify);
    }
    public void setContactChangeUnread(boolean isUnread){
        MmvkManger.getInstance().putBoolean(UserManager.get().getMyUserId()+"_unreadContact",isUnread);
    }

    public boolean getContactChangeUnread(){
        return MmvkManger.getInstance().getBoolean(UserManager.get().getMyUserId()+"_unreadContact",false);
    }
    //设置某群或者某用户信息免打扰
    public void setNotifyGroupOrUser(String groupIdorUserId,boolean isNotify){
        MmvkManger.getInstance().putBoolean(UserManager.get().getMyUserId()+groupIdorUserId+"_isNotify",isNotify);

    }
    //查询某人或者某用户是否被设置了免打扰
    public boolean getNotifyGroupOrUser(String groupIdorUserId){
      return   MmvkManger.getInstance().getBoolean(UserManager.get().getMyUserId()+groupIdorUserId+"_isNotify",true);
    }


    public void setVersoinStatus(JSONObject jsonObject){
        MmvkManger.getInstance().putJSON("VersoinStatus",jsonObject);
    }

    public JSONObject getVersionStatus(){
      return   MmvkManger.getInstance().getJSON("VersoinStatus");
    }
    //查询语音消息播放状态
    public boolean getSettingMsgSpeaker(){
        return   MmvkManger.getInstance().getBoolean("_isSpeaker",true);
    }

    public void setSettingMsgSpeaker(boolean isSpeaker){
        MmvkManger.getInstance().putBoolean("_isSpeaker",isSpeaker);
    }
    public void setCreateGroupAuthStatus(boolean status){
        MmvkManger.getInstance().putBoolean(UserManager.get().getMyUserId()+"_setCreateGroupAuthStatus",status);
    }

    public boolean getCreateGroupAuthStatus(){
        return  MmvkManger.getInstance().getBoolean(UserManager.get().getMyUserId()+"_setCreateGroupAuthStatus",false);
    }

}
