package com.fastchat.sdk.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @Description: 会话表
 * @Author: ouyang
 * @CreateDate: 2022/3/30 0030
 */
@Entity(tableName = "conversation")
public class ConversationModel {
    @PrimaryKey
    @NonNull
    private String userId;
    @ColumnInfo(name = "msgId")
    private String msgId;
    @ColumnInfo(name = "unReadCount")
    private int unReadCount;
    @ColumnInfo(name = "chatType")
    private int chatType;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "time_top")
    private long time_top;


    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime_top() {
        return time_top;
    }

    public void setTime_top(long time_top) {
        this.time_top = time_top;
    }

    @Override
    public String toString() {
        return "ConversationModel{" +
                "userId='" + userId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", unReadCount='" + unReadCount + '\'' +
                ", chatType='" + chatType + '\'' +
                ", time='" + time + '\'' +
                ", time_top='" + time_top + '\'' +
                '}';
    }
}
