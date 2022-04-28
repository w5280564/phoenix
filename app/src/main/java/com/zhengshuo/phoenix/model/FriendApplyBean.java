/** Copyright 2019 bejson.com */
package com.zhengshuo.phoenix.model;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FriendApplyBean {


    @SerializedName("applyStatus")
    private Integer applyStatus;
    @SerializedName("createTime")
    private Long createTime;
    @SerializedName("friendApplyId")
    private String friendApplyId;
    @SerializedName("friendUserId")
    private String friendUserId;
    @SerializedName("headImg")
    private String headImg;
    @SerializedName("imId")
    private String imId;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("remark")
    private String remark;
    @SerializedName("updateTime")
    private Long updateTime;
}
