package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

/** 用户登录返回结果 */
@lombok.NoArgsConstructor
@lombok.Data
public class LoginResultBean {


    @SerializedName("createTime")
    private Long createTime;
    @SerializedName("headImg")
    private String headImg;
    @SerializedName("imId")
    private String imId;
    @SerializedName("imPassword")
    private String imPassword;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("phoneNum")
    private String phoneNum;
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private String userId;
}
