package com.zhengshuo.phoenix.common.manager;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fastchat.sdk.manager.MmvkManger;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.util.StringUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ouyang on 2019/7/24.
 * 
 */
public class UserManager {

    private static UserManager loginUserManager;

    public static UserManager get() {
        if (loginUserManager == null) {
            loginUserManager = new UserManager();

        }
        return loginUserManager;

    }


    /**
     * 保存自己信息
     * @param jsonObject
     */
    public void saveMyUser(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        MmvkManger.getInstance().putJSON("KEY_LOGIN_USER_ALL", jsonObject);
        MmvkManger.getInstance().putString("KEY_LOGIN_USER_USERID", jsonObject.getString("userId"));
        MmvkManger.getInstance().putString("KEY_LOGIN_USER_NICK", jsonObject.getString("nickname"));
        MmvkManger.getInstance().putString("KEY_LOGIN_USER_AVATAR", jsonObject.getString("headImg"));
        MmvkManger.getInstance().putString("KEY_LOGIN_USER_TOKEN", jsonObject.getString("token"));
        MmvkManger.getInstance().putString("KEY_LOGIN_USER_IM_ID", jsonObject.getString("imId"));
        MmvkManger.getInstance().putString("KEY_USER_DEVICE_ID", getUUID());
    }


    public String getDeviceId() {
        String deviceId = "";
        String uuid = getMyDeviceId();;
        if (StringUtil.isBlank(uuid)) {
            deviceId = getUUID();
            MmvkManger.getInstance().putString("KEY_USER_DEVICE_ID", deviceId);
        }else {
            deviceId = uuid;
        }
        return deviceId;
    }


    private String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 清除自己信息
     */
    public void clearMyData() {
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_USERID");
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_ALL");
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_NICK");
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_AVATAR");
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_TOKEN");
        MmvkManger.getInstance().remove("KEY_LOGIN_USER_IM_ID");
        MmvkManger.getInstance().remove("payPasswordStatus");
    }


    public JSONObject getMyUser() {
        return MmvkManger.getInstance().getJSON("KEY_LOGIN_USER_ALL");
    }


    public String getMyUserId() {
        return MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_USERID");
    }

    public String getMyNick() {
        return MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_NICK");
    }

    public String getMyAvatar() {
        return MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_AVATAR");
    }

    public String getMyImId() {
        return MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_IM_ID");
    }

    public String getToken() {
        return MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_TOKEN");
    }

    private String getMyDeviceId() {
        return MmvkManger.getInstance().getAsString("KEY_USER_DEVICE_ID");
    }

    public void setUserRemark(String userId,String remark){
        if (remark != null) {
            MmvkManger.getInstance().putString(userId + "_remarkName", remark);
        }

    }


    public String getUserNick(String imId) {
        //先读本地
        String nick = MmvkManger.getInstance().getAsString(imId + "_remarkName");
        if (TextUtils.isEmpty(nick)) {
            return getUserRealNick(imId);
        }
        return nick;
    }


    public String getUserRealNick(String imId){
        //先读本地
        String nick = MmvkManger.getInstance().getAsString(imId + "_nickname");
        if (TextUtils.isEmpty(nick)) {
            getUserInfoFromServer(imId);
            return imId;
        }
        return nick;
    }

    public String getUserAvatar(String imId) {
        //先读本地
        String avatar = MmvkManger.getInstance().getAsString(imId + "_headImg");
        if (TextUtils.isEmpty(avatar)) {

            getUserInfoFromServer(imId);
            return "";
        }
        return avatar;
    }


    public String getUserImId(String userId){
        //先读本地
        String imId = MmvkManger.getInstance().getAsString(userId + "_imId");
        if (TextUtils.isEmpty(imId)) {
            getUserInfoFromServer(userId);
            return userId;
        }
        return imId;
    }


    public String getUserRemark(String imId) {

        return  MmvkManger.getInstance().getAsString(imId + "_remarkName");
    }



    private void getUserInfoFromServer(String userId) {
//        JSONObject data = new JSONObject();
//        data.put("friendUserId", userId);
//        ApiUtis.getInstance().postJSON(data, Constant.URL_USER_INFO, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//
//                if ("0".equals(jsonObject.getString("code"))) {
//                    String userId = jsonObject.getString("friendUserId");
//                    String nick = jsonObject.getString("nickname");
//                    String remark = jsonObject.getString("remarkName");
//                    String avatar = jsonObject.getString("headImg");
//                    //项目中如果备注存在，则直接显示用户的备注
//                    if (!TextUtils.isEmpty(remark)) {
//                        nick = remark;
//                    }
//                    //存本地
//                    MmvkManger.getIntance().putString(userId + "_nickname", nick);
//                    MmvkManger.getIntance().putString(userId + "_headImg", avatar);
//                    MmvkManger.getIntance().putString(userId + "_remarkName", remark);
////                    //发通知告知该用户资料已取得，在需要地方UI处更新头像昵称
////                    UserInfoEvent event = new UserInfoEvent();
////                    event.setUserId(userId);
////                    event.setNick(nick);
////                    event.setAvatar(avatar);
////                    EventBus.getDefault().postSticky(event);
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });

    }

    /**
     * 加载默认头像
     *
     * @param context
     * @param
     * @param imageView
     */
    public void loadUserAvatar(Context context, final String avatarUrl, final ImageView imageView) {
         if (context == null) {
             imageView.setImageResource(R.mipmap.error_image_placeholder);
            return;
        }

        if (TextUtils.isEmpty(avatarUrl)) {
            imageView.setImageResource(R.mipmap.error_image_placeholder);
            return;

        }

        Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.error_image_placeholder).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param
     * @param imageView
     */
    public void loadImage(Context context, final String avatarUrl, final ImageView imageView,int defaulResId) {
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(avatarUrl)) {
            imageView.setImageResource(defaulResId);
            return;

        }

        Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaulResId).into(imageView);
    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>他人信息开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 保存他人信息
     * @param userJson
     */
    public void saveUserInfo(JSONObject userJson) {
        String userId = userJson.getString("friendUserId");
        String nick = userJson.getString("nickname");
        String remark = userJson.getString("remarkName");
        String avatar = userJson.getString("headImg");
        String imId = userJson.getString("imId");

        MmvkManger.getInstance().putString(imId + "_nickname", nick);
        MmvkManger.getInstance().putString(imId + "_headImg", avatar);
        MmvkManger.getInstance().putString(imId + "_imId", imId);
        if (remark != null) {
            MmvkManger.getInstance().putString(imId + "_remarkName", remark);
        }
        MmvkManger.getInstance().putString(imId + "_userInfo", userJson.toJSONString());

    }
    public JSONArray getMyFrindsJsonArray(){
        JSONArray jsonArray=MmvkManger.getInstance().getJSONArray(getMyUserId() + "_MyFrinds");
        if(jsonArray==null){
            jsonArray=new JSONArray();
        }
        return jsonArray;
    }

    public void saveMyFrindsJsonArray(JSONArray jsonArray){
        MmvkManger.getInstance().putJSONArray(getMyUserId() + "_MyFrinds",jsonArray);
    }

    public void saveUserNickAvatar(String imId, String nick, String avatar) {
        MmvkManger.getInstance().putString(imId + "_nickname", nick);
        MmvkManger.getInstance().putString(imId + "_headImg", avatar);
    }

    public void saveFriends(Set<String> friends) {

        MmvkManger.getInstance().putStringSet(getMyUserId() + "_friends", friends);

    }


    public  void addMyFriends(String userId){
        Set<String> stringSet=getFriends();
        if(stringSet==null){
            stringSet=new HashSet<>();


        }
        stringSet.add(userId);
        saveFriends(stringSet);

        JSONArray jsonArray=getMyFrindsJsonArray();
        if(jsonArray.toJSONString().contains(userId)){
            return;
        }
        if(jsonArray==null){
           jsonArray=new JSONArray();
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("friendUserId",userId);
        jsonObject.put("nickname",UserManager.get().getUserNick(userId));
        jsonObject.put("headImg",UserManager.get().getUserAvatar(userId));
        jsonArray.add(jsonObject);
        saveMyFrindsJsonArray(jsonArray);
    }


    public  void  deleteMyFriends(String userId){
        Set<String> stringSet=getFriends();
        if(stringSet==null){
           return;
        }
        stringSet.remove(userId);
        saveFriends(stringSet);

        JSONArray jsonArray=getMyFrindsJsonArray();
        if(jsonArray==null){
           return;
        }
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(userId.equals(jsonObject.getString("friendUserId"))){

                jsonArray.remove(i);
                break;

            }
        }

        saveMyFrindsJsonArray(jsonArray);
    }


    public Set<String> getFriends() {
        Set<String> friends = MmvkManger.getInstance().getStringSet(getMyUserId() + "_friends");

        return friends != null ? friends : new HashSet<String>();
    }





}
