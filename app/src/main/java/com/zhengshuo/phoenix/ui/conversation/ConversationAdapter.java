package com.zhengshuo.phoenix.ui.conversation;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.widget.DragPointView;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageTextBody;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.manager.GroupInfoManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.ui.chat.weight.emojicon.SmileUtils;
import com.zhengshuo.phoenix.util.CommonUtils;
import com.zhengshuo.phoenix.util.DateUtils;
import com.zhengshuo.phoenix.util.GroupNoticeMessageUtils;
import com.zhengshuo.phoenix.widget.RoundImageView;
import java.util.Date;
import java.util.List;

/**
 * @Description:会话列表适配器
 * @Author: ouyang
 * @CreateDate: 2022/3/23 0009
 */
public class ConversationAdapter extends BaseQuickAdapter<HTConversation, BaseViewHolder> {

    public ConversationAdapter(List<HTConversation> htConversations) {
        super(R.layout.item_conversation,htConversations);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, HTConversation htConversation) {
        TextView tv_name = helper.getView(R.id.name);
        TextView tv_content = helper.getView(R.id.message);
        TextView tv_time = helper.getView(R.id.time);
        DragPointView tv_unread = helper.getView(R.id.unread_msg_number);
        RoundImageView ivAvatar =  helper.getView(R.id.avatar);
        ConstraintLayout re_main = helper.getView(R.id.re_main);
        TextView mentioned = helper.getView(R.id.mentioned);

        ChatType chatType = htConversation.getChatType();
        HTMessage htMessage = htConversation.getLastMessage();
//        List<HTMessage> messages = htConversation.getLastMessage();
//        if (messages != null && messages.size() > 0) {
//            htMessage = htConversation.getLastMessage();
//        }

//        if(htMessage==null){
//            htConversations.remove(position);
//            notifyDataSetChanged();
//            return convertView;
//        }
        String userId = htConversation.getUserId();
        if (chatType == ChatType.groupChat) {
            tv_name.setMaxLines(1);
            tv_name.setMaxEms(10);
            tv_name.setEllipsize(TextUtils.TruncateAt.END);
            ivAvatar.setImageResource(R.mipmap.default_group);
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
            if (htGroup != null) {
                tv_name.setText(htGroup.getGroupName());
                String imgUrl = htGroup.getGroupHeadImg();
//                if (!TextUtils.isEmpty(imgUrl)) {
//                    if (!imgUrl.startsWith("http") || !imgUrl.contains(HTConstant.baseImgUrl)) {
//                        imgUrl = HTConstant.baseImgUrl + imgUrl;
//                    }
//                }
                CommonUtils.loadGroupAvatar(mContext, imgUrl, ivAvatar);
            } else {
                //如果当前群还未获取到,
                if (htMessage != null && htMessage.getIntAttribute("action", 0) == 2000) {
                    String groupName = htMessage.getStringAttribute("groupName");
                    if (!TextUtils.isEmpty(groupName)) {
                        tv_name.setText(groupName);

                    }else{
                        tv_name.setText(userId);
                    }

                }else {
                    tv_name.setText(userId);
                }
                CommonUtils.loadGroupAvatar(mContext, null, ivAvatar);
            }

            if(!GroupInfoManager.getInstance().getAtTag(userId)){
                mentioned.setVisibility(View.GONE);
            }else {
                mentioned.setVisibility(View.VISIBLE);
            }

            //  tv_group_tag.setVisibility(View.INVISIBLE);
        } else {
            mentioned.setVisibility(View.GONE);
            ivAvatar.setImageResource(R.mipmap.default_avatar);
            String nick = UserManager.get().getUserNick(userId);
            String userAvatar =UserManager.get().getUserAvatar(userId);
            tv_name.setText(nick);
            UserManager.get().loadUserAvatar(mContext, userAvatar, ivAvatar);

        }
        if (htConversation.getUnReadCount() > 0) {
            // show unread message count
            tv_unread.setText(String.valueOf(htConversation.getUnReadCount()));
            tv_unread.setVisibility(View.VISIBLE);
        } else {
            tv_unread.setVisibility(View.INVISIBLE);
        }
        if (htMessage != null) {
            tv_content.setText(SmileUtils.getSmiledText(mContext, getContent(htMessage)),
                    TextView.BufferType.SPANNABLE);
            tv_time.setText(DateUtils.getTimestampString(new Date(htMessage.getTime())));
            // LoggerUtils.e("time----->"+htMessage.getTime()+"-----"+DateUtils.getTimestampString(new Date(htMessage.getTime())));
        } else {
            tv_content.setText("");
            tv_time.setText(DateUtils.getTimestampString(new Date(htConversation.getTime())));
        }
        if (htConversation.getTopTimestamp() != 0) {

            re_main.setBackgroundResource(R.drawable.list_item_bg_gray);
        } else {
            re_main.setBackgroundResource(R.drawable.list_item_bg_white);
        }
    }



    protected final static String[] msgs = {"发来一条消息", "[图片消息]", "[语音消息]", "[位置消息]", "[视频消息]", "[文件消息]",
            "%1个联系人发来%2条消息"
    };

    private String getContent(HTMessage message) {
        int action = message.getIntAttribute("action", 0);
        if (action > 1999 && action < 2005 || action == 3000||action==6001) {
            return GroupNoticeMessageUtils.getGroupNoticeContent(message);
        }
        String notifyText = "";
        if (message.getType() == null) {
            return "";
        }
        switch (message.getType()) {
            case TEXT:
                HTMessageTextBody textBody = (HTMessageTextBody) message.getBody();
                String content = textBody.getContent();
                if (content != null) {
                    notifyText += content;
                } else {
                    notifyText += msgs[0];
                }
                break;
            case IMAGE:
                notifyText += msgs[1];
                break;
            case VOICE:
                notifyText += msgs[2];
                break;
            case LOCATION:
                notifyText += msgs[3];
                break;
            case VIDEO:
                notifyText += msgs[4];
                break;
            case FILE:
                notifyText += msgs[5];
                break;
        }


        if (message.getChatType() == ChatType.groupChat) {
            //先从缓存取，缓存没有再从消息体中取
            String userId = message.getFrom();
            if (userId.equals(UserManager.get().getMyImId())) {
                return notifyText;
            }

            String nick = UserManager.get().getUserNick(userId);
            if (TextUtils.isEmpty(nick)) {
                //此处只需要判断nick是否为空，因为avatar即使本地存了，也是为空的
                nick = message.getStringAttribute("nick");
            }
            return String.format("%s：%s",nick,notifyText);
        }else{
            return notifyText;
        }
    }
}
