package com.fastchat.sdk;

/**
 * Created by ouyang on 2016/9/14.
 * 
 */
public class SDKConstant {

    public static final int REQUEST_SUCCESS_CODE = 200;//接口请求成功
    public static final int REQUEST_FAILURE_TOKEN = 201;//其他账号登录，被挤下线
    public static final int REQUEST_FAILURE_SERVER = -8885;//服务器错误(OKhttp报的错)
    public static final int REQUEST_FAILURE_INTERNET = -8884;//网络错误（网络连接不上）
    //是否是无限授权版本：
    public  static  final  boolean IS_LIMITLESS=false;
    //是否支持群离线消息
    public  static  final  boolean IS_GROUP_OFFLINE=false;

    //透传
    public static final  int TYPE_MESSAGE_CMD =1000;
    //正常消息体
    public static final  int TYPE_MESSAGE_HT =2000;
    //群被删除
    public static final  int TYPE_MESSAGE_GROUP_DESTROY =4000;
    //有人主动退出
    public static final  int TYPE_MESSAGE_GROUP_LEAVE =3000;
    //音视频通话透传消息
    public static final  int TYPE_CALL =5000;
    public  static final String FX_MSG_KEY_TYPE="type";
    public  static final String FX_MSG_KEY_DATA="data";
    public  static final String FX_MSG_KEY_TARGET="target";
    public  static final String FX_MSG_KEY_ERROR_MSG="errorMsg";
    public  static final String FX_MSG_KEY_RECEIPTS_REQUEST = "request";
    public  static final String FX_MSG_KEY_RECEIPTS_RECEIVED ="received";


    public static final String SERVER_NAME = "app.im";
    public static final String SERVER_DOMAIN = "@app.im";
    public static final String SERVER_DOMAIN_MUC = "@muc.app.im";
    public static final int PORT = 5222;

    public static final String HOST = "124.70.201.235";//api服务器ip
    //阿里云OSS
      public static final String accessKeyId = " ";
    public static final String accessKeySecret = " ";
    public static final String endpoint =" ";
    public static final String bucket = " ";
    public static final String URL_GROUP_LIST = "http://"+HOST+":8088/group/myAllGroupList";
    public static final String URL_UPLOAD_IMAGE = "http://"+HOST+":8088/file/uploadChatFile";//聊天图片上传


    public static final String baseOssUrl = "http://"+bucket+"."+endpoint+"/";

}

