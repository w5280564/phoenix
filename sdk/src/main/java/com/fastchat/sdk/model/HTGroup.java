package com.fastchat.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by ouyang on 2016/12/21.
 * 
 */

public class HTGroup implements Parcelable {
    protected String groupImId;
    protected String groupName;
    private String groupHeadImg;
    private String groupDesc;
    protected String owner;
    private long time;


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getGroupImId() {
        return groupImId;
    }

    public void setGroupImId(String groupImId) {
        this.groupImId = groupImId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected HTGroup(Parcel in) {
        groupImId = in.readString();
        groupName = in.readString();
        groupDesc = in.readString();
        owner = in.readString();
        time = in.readLong();
        groupHeadImg = in.readString();
    }

    public static final Creator<HTGroup> CREATOR = new Creator<HTGroup>() {
        @Override
        public HTGroup createFromParcel(Parcel in) {
            return new HTGroup(in);
        }

        @Override
        public HTGroup[] newArray(int size) {
            return new HTGroup[size];
        }
    };

    public HTGroup() {

    }

    public static HTGroup getHTGroup(JSONObject jsonObject) {
        HTGroup htGroup = new HTGroup();
        htGroup.setGroupName(jsonObject.getString("groupName"));
        htGroup.setGroupImId(jsonObject.getString("groupImId"));
        htGroup.setGroupHeadImg(jsonObject.getString("groupHeadImg"));
        return htGroup;
    }

    public void setGroupHeadImg(String groupHeadImg) {
        this.groupHeadImg = groupHeadImg;

    }

    public String getGroupHeadImg() {
        return groupHeadImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupImId);
        parcel.writeString(groupName);
        parcel.writeString(groupDesc);
        parcel.writeString(owner);
        parcel.writeLong(time);
        parcel.writeString(groupHeadImg);


    }

    @Override
    public String toString() {
        return groupImId;
    }
}
