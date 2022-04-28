package com.fastchat.sdk.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.db.DBManager;
import com.fastchat.sdk.db.dao.MessageDao;
import com.fastchat.sdk.db.model.MessageModel;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageBody;
import com.fastchat.sdk.utils.JsonUtil;
import com.fastchat.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 消息管理器
 * @Author: ouyang
 * @CreateDate: 2022/4/1
 */
public class MessageManager {
     private Context context;
    private MessageDao messageDao;
    private DBManager dbManager;

    public MessageManager(Context context) {
        this.context = context;
        dbManager = DBManager.getInstance(context);
        messageDao = dbManager.getMessageDao();
    }


    public void updateSuccess(HTMessage message) {
        message.setStatus(HTMessage.Status.SUCCESS);
        saveMessage(message, false);

    }

    public void deleteMessage(String chatTo, String msgId) {
        messageDao.deleteHTMessage(msgId);
    }



    public void deleteMessageFromTimestamp( String chatTo, long timeStamp) {
        messageDao.deleteHTMessageFromTimestamp(chatTo,timeStamp);
    }


    public void deleteUserMessage(String chatTo, boolean isDeleteConversation) {
        messageDao.deleteUserHTMessage(chatTo);
        if (isDeleteConversation) {
            HTClient.getInstance().conversationManager().deleteConversation(chatTo);
        }
     }


    /**
     * 获取会话所有消息
     * @param userId
     * @return
     */
    public List<HTMessage> getMessageList(final String userId) {
        List<MessageModel> mTempList = messageDao.getAllMessages(userId, ChatType.singleChat.ordinal());
        List<HTMessage> allMessage = new ArrayList<>();
        for (MessageModel messageTempModel : mTempList) {
            HTMessage message = convertMessageModelToHTMessage(messageTempModel);
            allMessage.add(0,message);
        }
        return allMessage;
    }



    //获取单条消息
    public HTMessage getMessage(String msgId) {
        MessageModel messageTempModel = messageDao.getMessage(msgId);
        HTMessage message = convertMessageModelToHTMessage(messageTempModel);
        return message;
    }

    //获取某个对话的最近的消息,以数据库为准
    public HTMessage getLastMessage(String chatTo) {
        MessageModel messageTempModel = messageDao.getLastMessage(chatTo,ChatType.singleChat.ordinal());
        HTMessage message = convertMessageModelToHTMessage(messageTempModel);
        return message;
    }

    /**
     * 把MessageModel转成HTMessage
     * @param messageTempModel
     * @return
     */
    @NonNull
    private HTMessage convertMessageModelToHTMessage(MessageModel messageTempModel) {
        if (messageTempModel != null) {
            HTMessage message = new HTMessage();
            message.setMsgId(messageTempModel.getMsgId());
            message.setBody(new HTMessageBody(messageTempModel.getBody()));
            message.setFrom(messageTempModel.getMsgFrom());
            message.setTo(messageTempModel.getMsgTo());
            message.setLocalTime(messageTempModel.getLocalTime());
            message.setTime(messageTempModel.getMsgTime());
            message.setReferenceId(messageTempModel.getReferenceId());
            int status = messageTempModel.getStatus();
            int type = messageTempModel.getType();

            int direct = messageTempModel.getDirect();
            int chatType = messageTempModel.getChatType();
            String attributes = messageTempModel.getAttribute();
            if (attributes != null) {
                try {
                    message.setAttributes(JSONObject.parseObject(attributes));
                } catch (JSONException e) {
                    message.setAttributes(new JSONObject());


                }
            }
            if (type == HTMessage.Type.TEXT.ordinal()) {
                message.setType(HTMessage.Type.TEXT);
            } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                message.setType(HTMessage.Type.IMAGE);
            } else if (type == HTMessage.Type.VOICE.ordinal()) {

                message.setType(HTMessage.Type.VOICE);
            } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                message.setType(HTMessage.Type.VIDEO);
            } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                message.setType(HTMessage.Type.LOCATION);
            } else if (type == HTMessage.Type.FILE.ordinal()) {

                message.setType(HTMessage.Type.FILE);
            } else {
                message.setType(HTMessage.Type.TEXT);
            }
            if (status == HTMessage.Status.CREATE.ordinal()) {

                message.setStatus(HTMessage.Status.CREATE);

            } else if (status == HTMessage.Status.FAIL.ordinal()) {
                message.setStatus(HTMessage.Status.FAIL);

            } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                message.setStatus(HTMessage.Status.SUCCESS);

            } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                message.setStatus(HTMessage.Status.INPROGRESS);
            } else if (status == HTMessage.Status.READ.ordinal()) {

                message.setStatus(HTMessage.Status.READ);
            } else if (status == HTMessage.Status.ACKED.ordinal()) {

                message.setStatus(HTMessage.Status.ACKED);
            }


            if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                message.setDirect(HTMessage.Direct.RECEIVE);
            } else {
                message.setDirect(HTMessage.Direct.SEND);
            }
            if (chatType == ChatType.singleChat.ordinal()) {
                message.setChatType(ChatType.singleChat);

            } else if (chatType == ChatType.groupChat.ordinal()) {
                message.setChatType(ChatType.groupChat);

            }
            return message;
        }
        return null;
    }


    public List<HTMessage> loadMoreMsgFromDB(String chatTo, long timestamp, int pageSize) {
        List<HTMessage> allMessage = new ArrayList<>();
        List<MessageModel> mTempList = messageDao.loadMoreMsgFromDB(chatTo, timestamp,ChatType.singleChat.ordinal(), pageSize);
        if (!mTempList.isEmpty()) {
            for (MessageModel messageTempModel:mTempList) {
                HTMessage message = convertMessageModelToHTMessage(messageTempModel);
                allMessage.add(0,message);
            }
        }
        return allMessage;
    }

    public List<HTMessage> searchMsgFromDB(String  content) {
        List<HTMessage> allMessage = new ArrayList<>();
        List<MessageModel> mTempList = messageDao.searchMsgFromDB(content);
        if (!mTempList.isEmpty()) {
            for (MessageModel messageTempModel : mTempList) {
                HTMessage message = convertMessageModelToHTMessage(messageTempModel);
                allMessage.add(message);
            }

        }
        return allMessage;
    }

    public synchronized void saveMessage(HTMessage htMessage, boolean isAddUnreadCount) {
        MessageModel mMessageModel = new MessageModel();
        mMessageModel.setMsgFrom(htMessage.getFrom());
        mMessageModel.setReferenceId(TextUtils.isEmpty(htMessage.getReferenceId())?"":htMessage.getReferenceId());
        mMessageModel.setMsgTo(htMessage.getTo());
        mMessageModel.setType(htMessage.getType().ordinal());
        mMessageModel.setMsgTime(htMessage.getTime());
        mMessageModel.setLocalTime(htMessage.getLocalTime());
        mMessageModel.setStatus(htMessage.getStatus().ordinal());
        mMessageModel.setBody(htMessage.getBody().getLocalBody());
        mMessageModel.setMsgId(htMessage.getMsgId());
        mMessageModel.setDirect(htMessage.getDirect().ordinal());
        mMessageModel.setChatType(htMessage.getChatType().ordinal());
        mMessageModel.setAttribute(htMessage.getAttributes().toJSONString());
        messageDao.saveMessage(mMessageModel);
        HTClient.getInstance().conversationManager().updateConversation(htMessage, isAddUnreadCount);
    }


    private List<HTMessage> loadMessageList(List<HTMessage> htMessages) {
        List<Pair<Long, HTMessage>> sortList = new ArrayList<Pair<Long, HTMessage>>();
        synchronized (htMessages) {
            for (HTMessage htMessage : htMessages) {
                try {
                    sortList.add(new Pair<Long, HTMessage>(htMessage.getTime(), htMessage));
                } catch (NullPointerException e) {


                }
            }
            try {
                // Internal is TimSort algorithm, has bug
                sortMessageByLastChatTime(sortList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<HTMessage> list = new ArrayList<HTMessage>();
            for (Pair<Long, HTMessage> sortItem : sortList) {
                list.add(sortItem.second);
            }
            return list;
        }

    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param
     */

    private void sortMessageByLastChatTime(List<Pair<Long, HTMessage>> messages) {
        Collections.sort(messages, new Comparator<Pair<Long, HTMessage>>() {
            @Override
            public int compare(final Pair<Long, HTMessage> con1, final Pair<Long, HTMessage> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return -1;
                } else {
                    return 1;
                }
            }

        });
    }


}
