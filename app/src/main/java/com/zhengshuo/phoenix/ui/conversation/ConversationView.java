package com.zhengshuo.phoenix.ui.conversation;

import android.view.View;

import com.fastchat.sdk.model.HTConversation;
import com.zhengshuo.phoenix.base.BaseView;

/**
 * Created by ouyang on 2017/6/27.
 * 
 */

public interface ConversationView extends BaseView<ConversationPresenter> {

    void showItemDialog(View view,HTConversation htConversation);

    void onReceiveNewMessage(int position, HTConversation mHTConversation,int type);

    void adapterRefresh();

}
