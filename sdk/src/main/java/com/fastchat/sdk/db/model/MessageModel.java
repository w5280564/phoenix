package com.fastchat.sdk.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @Description: 消息表
 * @Author: ouyang
 * @CreateDate: 2022/3/30 0030
 */
@Entity(tableName = "message")
public class MessageModel {

    @PrimaryKey
    @NonNull
    private String msgId;
    @ColumnInfo(name = "msgFrom")
    private String msgFrom;
    @ColumnInfo(name = "msgTo")
    private String msgTo;
    @ColumnInfo(name = "chatType")
    private int chatType;
    @ColumnInfo(name = "msgTime")
    private long msgTime;
    @ColumnInfo(name = "localTime")
    private long localTime;
    @ColumnInfo(name = "status")
    private int status;
    @ColumnInfo(name = "body")
    private String body;
    @ColumnInfo(name = "direct")
    private int direct;
    @ColumnInfo(name = "attribute")
    private String attribute;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "referenceId")
    private String referenceId;





    @NonNull
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(@NonNull String msgId) {
        this.msgId = msgId;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLocalTime() {
        return localTime;
    }

    public void setLocalTime(long localTime) {
        this.localTime = localTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "msgId='" + msgId + '\'' +
                ", msgFrom='" + msgFrom + '\'' +
                ", msgTo='" + msgTo + '\'' +
                ", body='" + body + '\'' +
                ", chatType='" + chatType + '\'' +
                ", msgTime='" + msgTime + '\'' +
                ", type='" + type + '\'' +
                ", localTime='" + localTime + '\'' +
                ", status='" + status + '\'' +
                ", direct='" + direct + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
