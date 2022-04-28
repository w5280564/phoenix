package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

public class UserInfoBean {
    private String id;

    private String portraitUri;

    @SerializedName(
            value = "name",
            alternate = {"nickname"})
    private String name;

    private String nameSpelling;

    private String nameSpellingInitial;

    private String alias;

    private String aliasSpelling;

    private String aliasSpellingInitial;

    private String region;

    private String phoneNumber;

    private int friendStatus;

    private String orderSpelling;

    private String stAccount;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStAccount() {
        return stAccount;
    }

    public void setStAccount(String stAccount) {
        this.stAccount = stAccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAliasSpelling() {
        return aliasSpelling;
    }

    public void setAliasSpelling(String aliasSpelling) {
        this.aliasSpelling = aliasSpelling;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public void setOrderSpelling(String orderSpelling) {
        this.orderSpelling = orderSpelling;
    }

    public String getOrderSpelling() {
        return orderSpelling;
    }

    public String getNameSpellingInitial() {
        return nameSpellingInitial;
    }

    public void setNameSpellingInitial(String nameSpellingInitial) {
        this.nameSpellingInitial = nameSpellingInitial;
    }

    public String getAliasSpellingInitial() {
        return aliasSpellingInitial;
    }

    public void setAliasSpellingInitial(String aliasSpellingInitial) {
        this.aliasSpellingInitial = aliasSpellingInitial;
    }

    @Override
    public String toString() {
        return "UserInfoBean{"
                + "id='"
                + id
                + '\''
                + ", portraitUri='"
                + portraitUri
                + '\''
                + ", name='"
                + name
                + '\''
                + ", nameSpelling='"
                + nameSpelling
                + '\''
                + ", nameSpellingInitial='"
                + nameSpellingInitial
                + '\''
                + ", alias='"
                + alias
                + '\''
                + ", aliasSpelling='"
                + aliasSpelling
                + '\''
                + ", aliasSpellingInitial='"
                + aliasSpellingInitial
                + '\''
                + ", region='"
                + region
                + '\''
                + ", phoneNumber='"
                + phoneNumber
                + '\''
                + ", friendStatus="
                + friendStatus
                + ", orderSpelling='"
                + orderSpelling
                + '\''
                + ", stAccount='"
                + stAccount
                + '\''
                + ", gender='"
                + gender
                + '\''
                + '}';
    }
}
