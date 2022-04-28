package com.fastchat.sdk.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @Description: 群组表
 * @Author: ouyang
 * @CreateDate: 2022/3/30 0030
 */
@Entity(tableName = "ht_group")
public class GroupModel {

    @PrimaryKey
    @NonNull
    private String groupId;
    @ColumnInfo(name = "groupName")
    private String groupName;
    @ColumnInfo(name = "desc")
    private String desc;
    @ColumnInfo(name = "owner")
    private String owner;
    @ColumnInfo(name = "time")
    private long time;

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(@NonNull String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "GroupModel{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", desc='" + desc + '\'' +
                ", owner='" + owner + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
