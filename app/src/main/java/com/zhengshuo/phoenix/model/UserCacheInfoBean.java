package com.zhengshuo.phoenix.model;


public class UserCacheInfoBean extends UserInfoBean {
    private String loginToken;
    private String password;

    public UserCacheInfoBean() {}

    public UserCacheInfoBean(String id) {
        setId(id);
    }

    public UserCacheInfoBean(
            String id,
            String loginToken,
            String phoneNumber,
            String password,
            String region) {
        setId(id);
        setPhoneNumber(phoneNumber);
        setLoginToken(loginToken);
        setRegion(region);
        setPassword(password);
    }

    public UserCacheInfoBean(String id, String token, String phone, String region) {
        setId(id);
        setPhoneNumber(phone);
        setLoginToken(token);
        setRegion(region);
    }

    public void setUserInfo(UserInfoBean info) {
        if (getId() != null && info != null && !getId().equals(info.getId())) {
            return;
        }
        setId(info.getId());
        setPortraitUri(info.getPortraitUri());
        setName(info.getName());
        setNameSpelling(info.getNameSpelling());
        setAlias(info.getAlias());
        setAliasSpelling(info.getAliasSpelling());
        setRegion(info.getRegion());
        setPhoneNumber(info.getPhoneNumber());
        setFriendStatus(info.getFriendStatus());
        setOrderSpelling(info.getOrderSpelling());
    }

    public void setUserCacheInfo(UserCacheInfoBean info) {
        setId(info.getId());
        setPortraitUri(info.getPortraitUri());
        setName(info.getName());
        setNameSpelling(info.getNameSpelling());
        setAlias(info.getAlias());
        setAliasSpelling(info.getAliasSpelling());
        setRegion(info.getRegion());
        setPhoneNumber(info.getPhoneNumber());
        setFriendStatus(info.getFriendStatus());
        setOrderSpelling(info.getOrderSpelling());
        setLoginToken(info.getLoginToken());
        setPassword(info.getPassword());
    }


    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
