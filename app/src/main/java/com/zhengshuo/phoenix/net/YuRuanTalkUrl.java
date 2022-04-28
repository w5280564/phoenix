package com.zhengshuo.phoenix.net;


import com.zhengshuo.phoenix.common.Config;

/**
 * @Description: api接口全局管理
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class YuRuanTalkUrl {

    public static final String DOMAIN = Config.Link.getWholeUrl();

    public static final String REGISTER_AND_LOGIN = "login/login";//登录并注册

    public static final String SEND_CODE = "login/smsCode";//发送验证码

    public static final String APPLY_FRIEND = "friend/apply";//申请添加好友

    public static final String AGREE_APPLY = "friend/agreeApply";//好友申请处理

    public static final String FRIEND_INDO = "friend/friendInfo";//好友详情

    public static final String FRIEND_SEARCH = "user/search";//搜索好友

    public static final String FRIEND_APPLY_LIST = "friend/applyList";//好友申请列表

    public static final String FRIEND_LIST = "friend/getFriendOrBlackList";//获取通讯录或黑名单列表

    public static final String CREATE_GROUP = "group/create";//创建群组

    public static final String MY_GROUP_LIST = "group/myGroupList";//我的群聊列表

    public static final String GROUP_USER_LIST = "group/userList";//群成员列表

    public static final String LOGIN_OUT = "user/out";//退出登录

    public static final String UPLOAD_RECORD = "http://124.70.201.235:8088/file/uploadRecord";//上传日志


}
