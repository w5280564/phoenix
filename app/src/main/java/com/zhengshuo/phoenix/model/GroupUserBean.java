package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: GroupUserBean
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0018
 */
@NoArgsConstructor
@Data
public class GroupUserBean {

    @SerializedName("headImg")
    private String headImg;
    @SerializedName("identity")
    private Integer identity;
    @SerializedName("imId")
    private String imId;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("userId")
    private String userId;
}
