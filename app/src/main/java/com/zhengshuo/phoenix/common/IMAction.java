package com.zhengshuo.phoenix.common;

/**
 * Created by ouyang on 2017/2/10.
 * 
 */

public class IMAction {
    public static final String ACTION_INVITE_MESSAGE = "action_invite_message";
    public static final String ACTION_NEW_MESSAGE = "action_new_message";
    public static final String ACTION_REMOVED_FROM_GROUP = "action_removed_from_group";
    public static final String ACTION_CONNECTION_CHANGED = "action_connection_changed";
    //消息撤回
    public static final String ACTION_MESSAGE_WITHDRAW = "action_message_withdraw";
    //删除好友通知
    public static final String CMD_DELETE_FRIEND = "DELETE_FRIEND";
    //被禁言
    public static final String ACTION_HAS_NO_TALK = "ACTION_HAS_NO_TALK";
    //离开或者退出删除群组
    public static final String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";
    //群主对群设置或者取消了管理员
    public static final String ACTION_SET_OR_CANCEL_GROUP_MANAGER = "ACTION_SET_OR_CANCEL_GROUP_MANAGER";
    //红包已领取
    public static final String RP_IS_HAS_OPENED = "RP_IS_HAS_OPENED";
    //群发布新公告
    public static final String NEW_GROUP_NOTICE = "NEW_GROUP_NOTICE";
    //好友设置了备注
    public static final String XMPP_LOGIN_OR_RE_LOGIN = "XMPP_LOGIN_OR_RE_LOGIN";
    public static final String NO_TALK_USER = "NO_TALK_USER";
    public static final String NO_TALK_USER_CANCEL = "NO_TALK_USER_CANCEL";
}
