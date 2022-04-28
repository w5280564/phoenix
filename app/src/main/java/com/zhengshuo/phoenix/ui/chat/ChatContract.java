package com.zhengshuo.phoenix.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fastchat.sdk.model.HTMessage;
import com.zhengshuo.phoenix.base.BasePresenter;
import com.zhengshuo.phoenix.base.BaseView;
import com.fastchat.sdk.model.ReferenceMessage;

import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public interface ChatContract {

    interface View extends BaseView<Presenter> {
        void showToast(int resId);

        void insertRecyclerView(int position, int count,int type);
        void showReferenceView(HTMessage htMessage);
        void hideReferenceView();

        void updateRecyclerView(int position );
        void loadMoreMessageRefresh(int position, int count);

        void initRecyclerView(List<HTMessage> messageList);
        void deleteItemRecyclerView(int position);
        //清空聊天记录刷新
        void notifyClear();
        void onGroupInfoLoaded();
        void showNewNoticeDialog(String title, String content, String id);
        void setAtUserStyle(String realNick, boolean isChooseFromList);
    }

    interface Presenter extends BasePresenter {
        void   sendZhenMessage();
        void initData(Bundle bundle);

        void loadMoreMessages();

         void sendTextMessage(String content,ReferenceMessage mReferenceMessageObj);
         void selectPicFromCamera(Activity activity);

        void selectPicFromLocal(Activity activity);
        void onResult(int requestCode, int resultCode, Intent data, Context context);

        void sendVoiceMessage(String voiceFilePath, int voiceTimeLength);

        void resendMessage(HTMessage htMessage);

        void deleteSingChatMessage(HTMessage htMessage);

        void referenceMessage(HTMessage htMessage);

        void switchVoicePlay(boolean isSpeakerPlay);

        void withdrawMessage(HTMessage htMessage, int position);

        void onMessageWithdraw(HTMessage htMessage);

        void onNewMessage(HTMessage htMessage);

        void onMessageForward(HTMessage htMessage);

        void onMessageClear();
        void getGroupInfoInServer(String groupId);

        void startCardSend(Activity activity);
        void setAtUser(String nick,String userId);
        boolean isHasAt(String userId);
        boolean isHasAtNick(String nick);
        void startChooseAtUser();
        void deleteAtUser(String nick);
        void refreshHistory();

    }
}
