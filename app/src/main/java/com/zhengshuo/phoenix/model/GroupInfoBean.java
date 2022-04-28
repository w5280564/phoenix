package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: GroupInfoBean
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0018
 */
@NoArgsConstructor
@Data
public class GroupInfoBean {


    @SerializedName("groupHeadImg")
    private String groupHeadImg;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("groupImId")
    private String groupImId;
    @SerializedName("groupIntroduction")
    private String groupIntroduction;
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("isAllMute")
    private Integer isAllMute;
    @SerializedName("isAnonymous")
    private Integer isAnonymous;
    @SerializedName("isInsideNotify")
    private Integer isInsideNotify;
    @SerializedName("isProtect")
    private Integer isProtect;
    @SerializedName("userId")
    private String userId;
}
