package com.zhengshuo.phoenix.ui.chat.provider;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageTextBody;

/**
 * 通知类消息（群通知，消息撤回等）
 */
public class NoticeMessageProvider extends BaseItemProvider <HTMessage, BaseViewHolder>{

    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_NOTICE;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_notice_message;
    }

    @Override
    public void convert(@NonNull BaseViewHolder helper, HTMessage item, int position) {
        TextView tv_time = helper.getView(R.id.tv_time);
        TextView tv_notice = helper.getView(R.id.tv_notice);

        tv_notice.setText(getGroupNoticeContent(item));
    }


    public HTMessage getItem(int position) {
        return mData.get(position);
    }


    private static String getGroupNoticeContent(HTMessage htMessage) {
        String content = "";
        //附加字段json
        int action = htMessage.getIntAttribute("action", 0);
        if (action != 0) {
            String groupName = htMessage.getStringAttribute("groupName");
            String uid = htMessage.getStringAttribute("uid");
            String nickName = htMessage.getStringAttribute("nickName");

            switch (action) {
                case 2000:
                    content = "群聊 " + "\"" + groupName + "\"" + " 创建成功";
                    break;
                case 2001:
                    if (BaseApplication.getInstance().getUsername().equals(uid)) {
                        content = "你 修改了群资料";
                    } else {
                        content = "\"" + nickName + "\"" + " 修改了群资料";
                    }
                    break;
                case 2003:
                    HTMessageTextBody body = (HTMessageTextBody) htMessage.getBody();
                    content = body.getContent();
                    break;
                case 2004:
                    content = "\"" + nickName + "\"" + " 被移除群聊";
                    break;

                case 6001:
                    String opId = htMessage.getStringAttribute("opId");
                    if (TextUtils.isEmpty(opId)) {
                        String userNick = htMessage.getStringAttribute("nick");
                        content = userNick + "撤回了一条消息";
                    } else {
                        String msgFrom = htMessage.getFrom();
                        if (opId.equals(msgFrom)) {
                            //如果操作者和消息发出者是同一个人
                            if (opId.equals(UserManager.get().getMyImId())) {
                                content = "你撤回了一条消息";
                            } else {
                                if (htMessage.getChatType() == ChatType.groupChat) {
                                    content = UserManager.get().getUserNick(opId) + "撤回了一条消息";
                                }else{
                                    content = "对方撤回了一条消息";
                                }
                            }
                        }

                    }
                    break;
            }
        }
        return content;
    }
}
