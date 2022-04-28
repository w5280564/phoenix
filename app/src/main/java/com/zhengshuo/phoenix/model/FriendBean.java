package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: FriendBean
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
@NoArgsConstructor
@Data
public class FriendBean {


    @SerializedName("friendUserId")
    private String friendUserId;
    @SerializedName("headImg")
    private String headImg;
    @SerializedName("imId")
    private String imId;
    @SerializedName("showId")
    private Integer showId;
    @SerializedName("remarkName")
    private String remarkName;
    @SerializedName("initialLetter")
    private String initialLetter;
}
