package com.zhengshuo.phoenix.common;

/**
 * @Description: 事件常量
 * @Author: ouyang
 * @CreateDate: 2022/3/23 0023
 */
public class EventConstants {

    public static final String  CREATE_GROUP = "createGroup";//创建群组
    public static final String  CONVERSATION_READ = "conversationRead";//会话已读
    public static final String  APP_ON_BACK = "isOnBack";//应用是否退到后台
    public static final String  ADD_NEW_FRIEND = "addNewFriend";//添加好友

    //来新消息
    public static final String ACTION_NEW_MESSAGE = "action_new_message";
    //消息撤回
    public static final String ACTION_MESSAGE_WITHDRAW = "action_message_withdraw";


    public static final String ACTION_INVITE_MESSAGE = "action_invite_message";

    public static final String ACTION_REMOVED_FROM_GROUP = "action_removed_from_group";
    public static final String ACTION_CONFLICT = "action_conflict";
    public static final String ACTION_CONNECTION_CHANGED = "action_connection_changed";

    //转发消息
    public static final String ACTION_MESSAGE_FORWARD = "action_message_forward";
    //清空消息
    public static final String ACTION_MESSAGE_EMPTY = "ACTION_MESSAGE_EMPTY";
    //删除好友通知
    public static final String CMD_DELETE_FRIEND = "DELETE_FRIEND";
    //本地删除好友
    public static final String DELETE_FRIEND_LOCAL = "DELETE_FRIEND_LOCAL";
    //资料更新的通知
    public static final String ACTION_UPDATE_INFO = "ACTION_UPDATE_INFO";
    /* //刷新所有列表
     public static final String ACTION_REFRESH_ALL_LIST = "ACTION_REFRESH_ALL_LIST";*/
    //解除禁言
    public static final String ACTION_HAS_CANCELED_NO_TALK = "ACTION_HAS_CANCELED_NO_TALK";
    //被禁言
    public static final String ACTION_HAS_NO_TALK = "ACTION_HAS_NO_TALK";

    //    //    备注好友
//    public static final String ACTION_REMARK_FRIEND = "ACTION_REMARK_FRIEND";
    //离开或者退出删除群组
    public static final String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";

    //    微信登录的Action
    public static final String LOGIN_BY_WECHAT_RESULT = "LOGIN_BY_WECHAT_RESULT";
    //删除了好友刷新通知
//    public static final String REFRESH_CONTACTS_LIST = "REFRESH_CONTACTS_LIST";
    //群主对群设置或者取消了管理员
    public static final String ACTION_SET_OR_CANCEL_GROUP_MANAGER = "ACTION_SET_OR_CANCEL_GROUP_MANAGER";
    //更新群资料
    public static final String ACTION_UPDATE_CHAT_TITLE = "ACTION_UPDATE_CHAT_TITLE";
    //添加好友的通知已经发出去
    public static final String ACTION_ADD_FRIEND_SEND = "ACTION_ADD_FRIEND_SEND";
    //设置支付密码成功
    public static final String SET_PAY_PWD_SUCCESS = "SET_PAY_PWD_SUCCESS";
    //红包已领取
    public static final String RP_IS_HAS_OPENED = "RP_IS_HAS_OPENED";
    // 二维码已付款
    public static final String QRCODE_IS_PAYED = "QRCODE_IS_PAYED";
    //    微信,支付宝支付的Action
    public static final String PAY_BY_WECHAT_RESULT = "PAY_BY_WECHAT_RESULT";
    public static final String VERSION_UPDATE = "VERSION_UPDATE";

    public static final String RED_PACKET_HAS_GOT = "RED_PACKET_HAS_GOT";
    //群发布新公告
    public static final String NEW_GROUP_NOTICE = "NEW_GROUP_NOTICE";
    //好友设置了备注
    public static final String USER_REMARK= "USER_REMARK";

    //好友设置了备注
    public static final String XMPP_LOGIN_OR_RE_LOGIN = "XMPP_LOGIN_OR_RE_LOGIN";

    public static final String NO_TALK_USER = "NO_TALK_USER";
    public static final String NO_TALK_USER_CANCEL = "NO_TALK_USER_CANCEL";
    public static final String REFRESH_LOCAL_MESSAGE = "REFRESH_LOCAL_MESSAGE";
}
