package com.fastchat.sdk.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.client.HTAction;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.db.DBManager;
import com.fastchat.sdk.db.dao.GroupDao;
import com.fastchat.sdk.db.model.GroupModel;
import com.fastchat.sdk.model.CmdMessage;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageBody;
import com.fastchat.sdk.utils.http.HttpSender;
import com.fastchat.sdk.utils.http.MyOnHttpResListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description: 群管理器
 * @Author: ouyang
 * @CreateDate: 2022/4/1
 */
public class GroupManager {

    private Context context;
    private GroupDao groupDao;
    private DBManager dbManager;
    private Map<String, HTGroup> allGroups = new HashMap<>();
    private String baseOssUrl = "";



    public GroupManager(Context context) {
        this.context = context;
        if (SDKConstant.IS_LIMITLESS) {
            baseOssUrl = HTPreferenceManager.getInstance().getOssBaseUrl();
        } else {
            baseOssUrl = SDKConstant.baseOssUrl;
        }

        dbManager = DBManager.getInstance(context);
        groupDao = dbManager.getGroupDao();
        initAllGroup();

    }


    //获取所有消息
    private void initAllGroup() {
        if (allGroups == null || allGroups.size() == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<GroupModel> mTempList = groupDao.getAllGroups();
                    if (!mTempList.isEmpty()) {
                        for (GroupModel mGroupModel:mTempList) {
                            String groupId = mGroupModel.getGroupId();
                            String groupName = mGroupModel.getGroupName();
                            String groupDesc = mGroupModel.getDesc();
                            String owner = mGroupModel.getOwner();
                            long time = mGroupModel.getTime();
                            HTGroup htGroup = new HTGroup();
                            htGroup.setOwner(owner);
                            htGroup.setGroupImId(groupId);
                            htGroup.setGroupName(groupName);
                            htGroup.setGroupDesc(groupDesc);
                            htGroup.setTime(time);
                            allGroups.put(groupId, htGroup);
                        }
                    }
                }
            }).start();
        }
        getGroupList(true, null);
    }


    private void getGroupList(final boolean isNotify, final GroupListCallBack groupListCallBack) {
        if (HTPreferenceManager.getInstance().getUser() == null) {
            return;
        }
        HashMap<String, String> baseMap = new HashMap<>();
        HttpSender sender = new HttpSender(SDKConstant.URL_GROUP_LIST, "获取所有群聊", baseMap,
                new MyOnHttpResListener() {
                    @Override
                    public void onComplete(JSONObject json_root, int status, String description) {
                        if (status == SDKConstant.REQUEST_SUCCESS_CODE) {
                            JSONArray groupArray = json_root.getJSONArray("data");
                            List<HTGroup> groupList = new ArrayList<HTGroup>();
                            for (int i = 0; i < groupArray.size(); i++) {
                                JSONObject group = groupArray.getJSONObject(i);
                                groupList.add(HTGroup.getHTGroup(group));
                            }
                            if (isNotify) {
                                saveGroupList(groupList);
                                Intent intent = new Intent();
                                intent.setAction(HTAction.ACTION_GROUPLIST_LOADED);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            }

                            if (groupListCallBack != null) {
                                groupListCallBack.onSuccess(groupList);
                                return;
                            }
                        }
                    }
                });
        sender.sendPost();
    }


    public void loadGroupListFromServer(GroupListCallBack groupListCallBack) {
        getGroupList(false, groupListCallBack);
    }


    public interface GroupListCallBack {
        void onSuccess(List<HTGroup> htGroups);
        void onFailure();
    }


    public HTGroup getGroup(String groupId) {
        return allGroups.get(groupId);
    }


    // 获取所有群组
    public List<HTGroup> getAllGroups() {
        if (allGroups == null) {
            new ArrayList<>();
        }
        return new ArrayList<HTGroup>(allGroups.values());
    }


    public synchronized void saveGroup(HTGroup htGroup) {
        allGroups.put(htGroup.getGroupImId(), htGroup);
        GroupModel mGroupModel = convertHTGroupToGroupModel(htGroup);
        groupDao.saveGroup(mGroupModel);
    }


    /**
     * 把HTGroup转成GroupModel
     * @param htGroup
     * @return
     */
    @NonNull
    private GroupModel convertHTGroupToGroupModel(HTGroup htGroup) {
        String groupId = htGroup.getGroupImId();
        String groupName = htGroup.getGroupName();
        String groupDesc = htGroup.getGroupDesc();
        String owner = htGroup.getOwner();
        long time = htGroup.getTime();
        GroupModel mGroupModel = new GroupModel();
        mGroupModel.setOwner(owner);
        mGroupModel.setGroupId(groupId);
        mGroupModel.setGroupName(groupName);
        mGroupModel.setDesc(groupDesc);
        mGroupModel.setTime(time);
        return mGroupModel;
    }

    public synchronized void saveGroupList(List<HTGroup> htGroups) {
        List<GroupModel> groupModelList = new ArrayList<>();
        allGroups.clear();
        for (HTGroup htGroup : htGroups) {
            allGroups.put(htGroup.getGroupImId(), htGroup);
            GroupModel mGroupModel = convertHTGroupToGroupModel(htGroup);
            groupModelList.add(mGroupModel);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                groupDao.saveGroupList(groupModelList);
            }
        }).start();

    }

    public void deleteGroupLocalOnly(String groupId) {
        groupDao.deleteGroup(groupId);
        allGroups.remove(groupId);
        HTClient.getInstance().messageManager().deleteUserMessage(groupId, true);
    }


    public interface CallBack {
        void onSuccess(String data);
        void onFailure();
        void onHTMessageSend(HTMessage htMessage);
    }


    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1000:

                    getGroupList(true, null);
                    break;

            }
        }
    };


    public static String filterEmoji(String source,String slipStr) {
        if(containsEmoji(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        }else{
            return source;
        }
    }
    public static boolean containsEmoji(String value){
        boolean flag = false;
        try {
            Pattern p = Pattern
                    .compile("[^\\u0000-\\uFFFF]");
            Matcher m = p.matcher(value);
            flag = m.find();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }





    public void updateGroupName(String groupId, String groupName, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, groupName, htGroup.getGroupDesc(), htGroup.getGroupHeadImg(), null, nickName, callBack);
    }

    public void updateGroupDesc(String groupId, String groupDesc, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, htGroup.getGroupName(), groupDesc, htGroup.getGroupHeadImg(), null, nickName, callBack);
    }

    public void updateGroupImgUrlRemote(String groupId, String imgUrl, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, htGroup.getGroupName(), htGroup.getGroupDesc(), imgUrl, null, nickName, callBack);
    }

    public void updateGroupImgUrlLocal(String groupId, String filePath, String nickName, final CallBack callBack) {


        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }


        updateGroupInfo(groupId, htGroup.getGroupName(), htGroup.getGroupDesc(), null, filePath, nickName, callBack);
    }

    private void updateGroupInfo(final String groupId, final String groupNameTemp, String groupDesc, String imgUrl, String filePath, final String nickName, final CallBack callBack) {
        if (TextUtils.isEmpty(groupId)) {
            callBack.onFailure();
            Toast.makeText(context, "groupId can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(groupNameTemp)) {
            callBack.onFailure();
            Toast.makeText(context, "groupName can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (groupDesc == null) {
            groupDesc = "";
        }
         final String  groupName=filterEmoji(groupNameTemp,"");

        final String finalGroupDesc = groupDesc;
//        if (!TextUtils.isEmpty(filePath)) {
//            final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//            new UploadFileUtils(context, fileName, filePath).uploadFile(new UploadFileUtils.UploadCallBack() {
//
//                @Override
//                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                    Log.d("UploadFileUtils", "onProgress");
//                }
//
//                @Override
//                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                    Log.d("UploadFileUtils", "onSuccess");
//                    updateGroupInfoInteral(groupId, groupName, finalGroupDesc, baseOssUrl + fileName, nickName, callBack);
//                }
//
//                @Override
//                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                    Log.d("UploadFileUtils", "onFailure");
//                    callBack.onFailure();
//                }
//            });
//
//        } else {
//            updateGroupInfoInteral(groupId, groupName, finalGroupDesc, imgUrl, nickName, callBack);
//
//        }//todo

    }





    private void sendNoticeMessage(JSONObject extJson, String groupId, String content, final CallBack callBack) {
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


    public interface GroupOperationCallBack {
        void callBack(HTMessage htMessage);
    }

    ;

    private void sendCustomMessage(String body, ChatType chatType, String chatTo) {
        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(body);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        customMessage.setTo(chatTo);
        customMessage.setChatType(chatType);
//       HTMessageHelper.sendCustomMessage(customMessage,context);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
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


}
