package com.fastchat.sdk.manager;

import android.content.Context;
import android.util.Pair;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.db.DBManager;
import com.fastchat.sdk.db.dao.ConversationDao;
import com.fastchat.sdk.db.model.ConversationModel;
import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 会话管理器
 * @Author: ouyang
 * @CreateDate: 2022/4/1
 */
public class ConversationManager {
     private static ConversationDao htConversationDao;
    private DBManager dbManager;
    private static Map<String, HTConversation> allConversations = new HashMap<>();


    public ConversationManager(Context context) {
         dbManager = DBManager.getInstance(context);
         htConversationDao = dbManager.getConversationDao();
         initAllConversations();
    }



    private void initAllConversations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ConversationModel> mTempList = htConversationDao.getConversationList();
                if (!mTempList.isEmpty()) {
                    Map<String, HTConversation> tempAllConversations = new HashMap<>();
                    for (ConversationModel mConversationModel:mTempList) {
                        String userId = mConversationModel.getUserId();
                        int unReadCount = mConversationModel.getUnReadCount();
                        long topTimestamp = mConversationModel.getTime_top();
                        long time = mConversationModel.getTime();
                        int chatType = mConversationModel.getChatType();
                        HTConversation htConversation = new HTConversation();
                        htConversation.setUserId(userId);
                        if (chatType == ChatType.singleChat.ordinal()) {
                            htConversation.setChatType(ChatType.singleChat);
                        } else if (chatType == ChatType.groupChat.ordinal()) {
                            htConversation.setChatType(ChatType.groupChat);
                        }
                        htConversation.setUnReadCount(unReadCount);
                        htConversation.setTopTimestamp(topTimestamp);
                        htConversation.setTime(time);
                        tempAllConversations.put(userId, htConversation);
                    }
                    allConversations = tempAllConversations;
                }
            }
        }).start();
    }

    public void markAllMessageRead(String userId) {
        HTConversation htConversation = allConversations.get(userId);
        if (htConversation != null) {

            htConversation.setUnReadCount(0);
            refreshConversationList(userId, htConversation);
        }
    }

    public List<HTConversation> getAllConversations() {
        if (allConversations == null) {
            allConversations = new HashMap<>();
        }

        return loadConversationList(allConversations);
    }


    public HTConversation getConversation(String userId) {
        HTConversation htConversation = null;
        if (allConversations != null) {
            htConversation = allConversations.get(userId);
        }

        return htConversation;
    }

    public void updateConversation(HTMessage message, boolean isAddUnreadCount) {

        HTConversation htConversation=null;
        String userId = message.getUsername();

        if (allConversations.containsKey(userId)) {
            htConversation=allConversations.get(userId);
            htConversation.setChatType(message.getChatType());
            htConversation.setTime(message.getTime());
            int currentUnreadCount = allConversations.get(userId).getUnReadCount();
            //已有会话数据
            if (isAddUnreadCount) {
                //需要增加未读条数
                htConversation.setUnReadCount(currentUnreadCount + 1);
            } else {
                //不需要增加未读条数
                htConversation.setUnReadCount(currentUnreadCount);
            }
        } else {
            htConversation = new HTConversation();
            HTMessage lastMessage = HTClient.getInstance().messageManager().getLastMessage(htConversation.getUserId());
            if (lastMessage!=null) {
                htConversation.setLastMessage(lastMessage);
            }
            htConversation.setUserId(userId);
            htConversation.setChatType(message.getChatType());
            htConversation.setTime(message.getTime());
            //还未创建会话列表
            if (isAddUnreadCount) {
                //需要增加未读条数
                htConversation.setUnReadCount(1);
            } else {
                //不需要增加未读条数
                htConversation.setUnReadCount(0);
            }
        }
        refreshConversationList(userId, htConversation);
    }

    public void refreshConversationList(final String userId, final HTConversation htConversation) {
        allConversations.put(userId, htConversation);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConversationModel mConversationModel = new ConversationModel();
                mConversationModel.setChatType(htConversation.getChatType().ordinal());
                mConversationModel.setTime(htConversation.getTime());
                mConversationModel.setTime_top(htConversation.getTopTimestamp());
                mConversationModel.setUserId(htConversation.getUserId());
                mConversationModel.setUnReadCount(htConversation.getUnReadCount());
                htConversationDao.saveConversation(mConversationModel);
            }
        }).start();

    }

    public List<HTConversation> loadConversationList(Map<String, HTConversation> htConversationMap) {

            if(htConversationMap==null||htConversationMap.size()==0){
                return new ArrayList<>();
            }
            return new ArrayList<>(htConversationMap.values()) ;


    }

    public void deleteConversationAndMessage(String chatTo) {

        HTClient.getInstance().messageManager().deleteUserMessage(chatTo, true);
    }


    public void deleteConversation(String chatTo) {
        htConversationDao.deleteConversation(chatTo);
        if (allConversations.containsKey(chatTo)) {
            allConversations.remove(chatTo);

        }
    }


    public void sortConversationByLastChatTime(List<Pair<Long, HTConversation>> messages) {
        Collections.sort(messages, new Comparator<Pair<Long, HTConversation>>() {
            @Override
            public int compare(final Pair<Long, HTConversation> con1, final Pair<Long, HTConversation> con2) {
                if(con2.second.getTopTimestamp()!=0&&con1.second.getTopTimestamp()!=0){
                    //都是置顶消息
                    if (con1.first == con2.first) {
                        return 0;
                    } else if (con2.first > con1.first) {
                        return 1;
                    } else {
                        return -1;
                    }
                }else if(con2.second.getTopTimestamp()!=0&&con1.second.getTopTimestamp()==0){
                    return 1;
                }else if(con2.second.getTopTimestamp()==0&&con1.second.getTopTimestamp()!=0) {
                    return -1;
                }else if(con2.second.getTopTimestamp()==0&&con1.second.getTopTimestamp()==0){
                    //都是置顶消息
                    if (con1.first == con2.first) {
                        return 0;
                    } else if (con2.first > con1.first) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
               return 0;
            }

        });
    }

    public void setConversationTop(HTConversation  htConversation,long timestamp){
        htConversation.setTopTimestamp(timestamp);
        saveConversation(htConversation);

    }

    private void saveConversation(HTConversation htConversation){
        allConversations.put(htConversation.getUserId(),htConversation);
        ConversationModel mConversationModel = new ConversationModel();
        mConversationModel.setChatType(htConversation.getChatType().ordinal());
        mConversationModel.setTime(htConversation.getTime());
        mConversationModel.setTime_top(htConversation.getTopTimestamp());
        mConversationModel.setUserId(htConversation.getUserId());
        mConversationModel.setUnReadCount(htConversation.getUnReadCount());
        htConversationDao.saveConversation(mConversationModel);

    }


    public void clearAllConversations() {
        if (allConversations == null) {
            return;
        }
        allConversations.clear();
    }
}
