package com.zhengshuo.phoenix.ui.conversation;

import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTMessage;
import com.zhengshuo.phoenix.base.BasePresenter;

import java.util.List;

/**
 * Created by ouyang on 2017/6/27.
 * 
 */

public interface BaseConversationPresenter extends BasePresenter {
    List<HTConversation> getAllConversations();
    void deleteConversation(String userId);
    void setTopConversation(HTConversation conversation);
    void cancelTopConversation(HTConversation conversation);
   // void refreshConversations();
    void onNewMsgReceived(HTMessage htmessage);
    int getUnreadMsgCount();
    void markAllMessageRead(HTConversation conversation);
    void refreshContactsInServer();
    void requestSmallProgram(int page);
    void onMsgWithDraw(HTMessage htMessage);
    void checkFriendsAndGroups();
}
