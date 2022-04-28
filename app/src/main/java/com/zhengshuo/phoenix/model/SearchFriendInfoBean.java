package com.zhengshuo.phoenix.model;

import java.io.Serializable;

/**
 * @Description: 搜索好友
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0014
 */
public class SearchFriendInfoBean implements Serializable {
    private String imId;
    private String nickname;
    private String headImg;
    private String userId;
    private int friendStatus;


    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }
}
