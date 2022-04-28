package com.zhengshuo.phoenix.task;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.GroupManager;
import com.fastchat.sdk.manager.HTChatManager;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageBody;
import com.zhengshuo.phoenix.model.BaseResultBean;
import com.zhengshuo.phoenix.model.GroupInfoBean;
import com.zhengshuo.phoenix.model.GroupListTreeBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.net.HttpClientManager;
import com.zhengshuo.phoenix.net.service.GroupService;
import com.zhengshuo.phoenix.net.NetworkOnlyResource;
import com.zhengshuo.phoenix.net.RetrofitUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.RequestBody;

/**
 * @Description: 群相关业务处理
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class GroupTask {
    private GroupService groupService;
    // 存储当前最新一次登录的用户信息

    public GroupTask(Context context) {
        groupService =
                HttpClientManager.getInstance(context).getClient().createService(GroupService.class);
    }




    /**
     * 创建群组
     *
     * @param groupName 群名称
     * @param userIds 群成员
     * @return
     */
    public LiveData<Resource<GroupInfoBean>> createGroup(String groupName,String groupIntroduction, ArrayList<String> userIds) {
        MediatorLiveData<Resource<GroupInfoBean>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Resource<GroupInfoBean>> login =
                new NetworkOnlyResource<GroupInfoBean, BaseResultBean<GroupInfoBean>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<GroupInfoBean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("groupName", groupName);
                paramsMap.put("groupIntroduction", groupIntroduction);
                paramsMap.put("userIds", userIds);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.createGroup(body);
            }
        }.asLiveData();
        result.addSource(
                login,
                new Observer<Resource<GroupInfoBean>>() {
                    @Override
                    public void onChanged(Resource<GroupInfoBean> groupResultResource) {
                        if (groupResultResource.status == Status.SUCCESS) {
                            result.removeSource(login);
                            GroupInfoBean groupResult = groupResultResource.data;
                            if (groupResult!= null) {
                                HTGroup htGroup = new HTGroup();
                                htGroup.setGroupDesc(groupIntroduction);
                                htGroup.setTime(System.currentTimeMillis());
                                htGroup.setGroupImId(groupResult.getGroupImId());
                                htGroup.setGroupHeadImg(groupResult.getGroupHeadImg());
                                htGroup.setGroupName(groupName);
                                htGroup.setOwner(HTPreferenceManager.getInstance().getUser().getUsername());
                                HTClient.getInstance().groupManager().saveGroup(htGroup);

                                JSONObject extJson = new JSONObject();
                                extJson.put("action", 2000);
                                extJson.put("groupName", groupName);
                                extJson.put("groupDescription", groupIntroduction);
//                                extJson.put("groupAvatar", imgUrl);
//                                sendNoticeMessage(extJson, groupResult.getGroupImId(), creatorNick + "创建了群聊:" + groupName);
                                result.postValue(Resource.success(groupResult,"创建成功"));
                            } else {
                                result.setValue(Resource.error(groupResultResource.code, null,groupResultResource.message));
                            }
                        } else if (groupResultResource.status == Status.ERROR) {
                            result.setValue(Resource.error(groupResultResource.code,null, groupResultResource.message));
                        } else {
                            // do nothing
                        }
                    }
                });
        return result;
    }



    /**
     * 我的群组列表
     * @return
     */
    public LiveData<Resource<GroupListTreeBean>> getMyGroupList() {
        return new NetworkOnlyResource<GroupListTreeBean, BaseResultBean<GroupListTreeBean>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<GroupListTreeBean>> createCall() {
                return groupService.getMyGroupList();
            }
        }.asLiveData();
    }


    /**
     * 获取群成员列表
     * @return
     */
    public LiveData<Resource<JSONArray>> getGroupUserList(String groupImId) {
        return new NetworkOnlyResource<JSONArray, BaseResultBean<JSONArray>>() {

            @NonNull
            @Override
            protected LiveData<BaseResultBean<JSONArray>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("groupImId", groupImId);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return groupService.getGroupUserList(body);
            }
        }.asLiveData();
    }


    private void sendNoticeMessage(JSONObject extJson, String groupId, String content, final GroupManager.CallBack callBack) {
        final HTMessage htMessage = new HTMessage();
        String msgId = UUID.randomUUID().toString();
        htMessage.setAttributes(extJson);
        JSONObject textJson = new JSONObject();
        textJson.put("content", content);
        htMessage.setBody(new HTMessageBody(textJson));
        htMessage.setStatus(HTMessage.Status.SUCCESS);
        htMessage.setTime(System.currentTimeMillis());
        htMessage.setTo(groupId);
        htMessage.setMsgId(msgId);
        htMessage.setChatType(ChatType.groupChat);
        htMessage.setType(HTMessage.Type.TEXT);
        htMessage.setLocalTime(System.currentTimeMillis());
        htMessage.setDirect(HTMessage.Direct.SEND);
        htMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        //  HTMessageHelper.sendHTMessage(htMessage,context);

        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
                htMessage.setTime(timeStamp);
                if (callBack != null) {
                    callBack.onHTMessageSend(htMessage);
                }
//               HTClient.getInstance().messageManager().saveMessage(htMessage,false);
//                       List<Param> params = new ArrayList<>();
//                       String chatType = "1";
//                       if (htMessage.getChatType() == ChatType.groupChat) {
//                           chatType = "2";
//                           params.add(new Param("mid", htMessage.getMsgId()));
//                       }
//                       params.add(new Param("fromId", htMessage.getFrom()));
//                       params.add(new Param("toId", htMessage.getTo()));
//                       params.add(new Param("chattype", chatType));
//                       params.add(new Param("timeStamp", String.valueOf(htMessage.getTime())));
//                       String msgString = Base64.encode(htMessage.toXmppMessageBody());
//                       params.add(new Param("message", msgString));
//                       new NewOkHttpUtils().post(params, SDKConstant.HOST_API, new NewOkHttpUtils.HttpCallBack() {
//                           @Override
//                           public void onResponse(JSONObject jsonObject) {
//                              Log.d(" 上传聊天记录jsonObject:",jsonObject.toJSONString());
//                           }
//
//                           @Override
//                           public void onFailure(String errorMsg) {
//
//                           }
//                       });
////


            }

            @Override
            public void onFailure() {

            }
        });
    }

}
