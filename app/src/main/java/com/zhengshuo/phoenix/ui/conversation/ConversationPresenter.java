package com.zhengshuo.phoenix.ui.conversation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ouyang on 2017/6/27.
 * 
 */

public class ConversationPresenter implements BaseConversationPresenter {
    private ConversationView conversationView;
    private List<HTConversation> allConversations = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //本地会话数据库查询结束

                    List<HTConversation> htConversations = (List<HTConversation>) msg.obj;
                    allConversations.clear();
                    allConversations.addAll(htConversations);
                    conversationView.adapterRefresh();
                    break;

            }
        }
    };

    public ConversationPresenter(ConversationView view) {
        conversationView = view;
        conversationView.setPresenter(this);
        // loadAllConversation();
    }


    @Override
    public List<HTConversation> getAllConversations() {

        return allConversations;
    }

    @Override
    public void deleteConversation(final String userId) {
        for (HTConversation htConversation : allConversations) {
            if (htConversation.getUserId().equals(userId)) {
                allConversations.remove(htConversation);
                conversationView.adapterRefresh();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HTClient.getInstance().messageManager().deleteUserMessage(userId, true);

                    }
                }).start();

                break;
            }
        }


    }

    @Override
    public void setTopConversation(final HTConversation htConversation) {
        htConversation.setTopTimestamp(System.currentTimeMillis());
        int originPosition = allConversations.indexOf(htConversation);
        if (originPosition != -1) {
            allConversations.remove(originPosition);
        }
        allConversations.add(0, htConversation);
        conversationView.adapterRefresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HTClient.getInstance().conversationManager().setConversationTop(htConversation, System.currentTimeMillis());
            }
        }).start();

        // conversationView.refreshALL();

    }

    @Override
    public void cancelTopConversation(HTConversation htConversation) {
        htConversation.setTopTimestamp(0);
        conversationView.adapterRefresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HTClient.getInstance().conversationManager().setConversationTop(htConversation, 0);
            }
        }).start();
        //  conversationView.refreshALL();
    }

    @Override
    public void onNewMsgReceived(HTMessage htMessage) {
        if (htMessage != null) {
            //根据消息获取会话id;
            String currentConvId = htMessage.getUsername();
            //计算置顶数目
            int currentTopCount=0;
            for( HTConversation easeConversationInfo:allConversations){
                if(easeConversationInfo.getTopTimestamp() > 0){
                    currentTopCount++;
                }
            }

            //是否已经有针对这个消息对象的会话

            int index= -1;

            for(int i=0;i<allConversations.size();i++){
                HTConversation mConversationInfo = allConversations.get(i);
                if(mConversationInfo.getUserId().equals(currentConvId)){
                    index=i;
                    HTConversation mConversationTemp = HTClient.getInstance().conversationManager().getConversation(currentConvId);

                    HTConversation newConversation = new HTConversation();
                    newConversation.setTime(System.currentTimeMillis());
                    newConversation.setChatType(mConversationTemp.getChatType());
                    newConversation.setUnReadCount(mConversationTemp.getUnReadCount());
                    newConversation.setTopTimestamp(mConversationTemp.getTopTimestamp());
                    newConversation.setLastMessage(htMessage);
                    newConversation.setUserId(mConversationTemp.getUserId());
                    //先删除原位置
                    allConversations.remove(index);
                    allConversations.add(index, newConversation);
                    conversationView.onReceiveNewMessage(index, null,1);
                    break;
                }
            }
            if(index==-1){
                //不存在则插入到最新的位置
                HTConversation newConversation = new HTConversation();
                newConversation.setTime(System.currentTimeMillis());
                newConversation.setChatType(htMessage.getChatType());
                newConversation.setUnReadCount(1);
                newConversation.setLastMessage(htMessage);
                newConversation.setUserId(currentConvId);
                conversationView.onReceiveNewMessage(currentTopCount, newConversation,2);
            }
        }
    }



    @Override
    public int getUnreadMsgCount() {
        int unreadMsgCountTotal = 0;
        if (allConversations.size() != 0 && allConversations != null) {
            for (int i = 0; i < allConversations.size(); i++) {
                unreadMsgCountTotal += allConversations.get(i).getUnReadCount();
            }
        }
        return unreadMsgCountTotal;
    }

    @Override
    public void markAllMessageRead(HTConversation conversation) {
        HTClient.getInstance().conversationManager().markAllMessageRead(conversation.getUserId());
        conversation.setUnReadCount(0);
        conversationView.adapterRefresh();
    }


    @Override
    public void refreshContactsInServer() {


    }

    /**
     * 加载会话
     */
    private void loadAllConversation() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handleConversationList();
            }
        });
        thread.start();
    }


    @Override
    public void requestSmallProgram(int page) {

    }

    @Override
    public void onMsgWithDraw(HTMessage htMessage) {

    }

    @Override
    public void checkFriendsAndGroups() {
        loadAllConversation();
    }

    private void handleConversationList() {
        if (!HTClient.getInstance().isLoginEd()) {
            return;
        }

        List<HTConversation> htConversations = HTClient.getInstance().conversationManager().getAllConversations();
        if (htConversations == null || htConversations.size() == 0) {
            return;
        }

        ListIterator<HTConversation> iterator = htConversations.listIterator();
        while (iterator.hasNext()) {
            HTConversation htConversation = iterator.next();
            HTMessage lastMessage = HTClient.getInstance().messageManager().getLastMessage(htConversation.getUserId());
            if (lastMessage != null) {
                htConversation.setLastMessage(lastMessage);
            }
            if (htConversation.getChatType() == ChatType.groupChat) {
                HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(htConversation.getUserId());
                if (htGroup == null) {
                    iterator.remove();
                }
            }
        }
        Collections.sort(htConversations, new Comparator<HTConversation>() {
            @Override
            public int compare(HTConversation o1, HTConversation o2) {
                long topTime1 = o1.getTopTimestamp();
                long topTime2 = o2.getTopTimestamp();
                if (topTime1 > topTime2) {
                    return -1;
                } else if (topTime1 == topTime2) {
                    long time1 = o1.getTime();
                    long time2 = o2.getTime();
                    if (time1 > time2) {
                        return -1;
                    } else if (time1 == time2) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {

                    return 1;
                }
            }
        });
        Message message = handler.obtainMessage();
        message.obj = htConversations;
        message.what = 1000;
        message.sendToTarget();
    }

    @Override
    public void start() {
        loadAllConversation();
    }

    private int getTopCount() {
        int count = 0;
        for (HTConversation htConversation : allConversations) {
            if (htConversation.getTopTimestamp() > 0) {
                count++;
            } else {
                break;
            }
        }


        return count;

    }
}
