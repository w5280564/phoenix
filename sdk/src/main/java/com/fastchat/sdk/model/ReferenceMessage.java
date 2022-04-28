package com.fastchat.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;

public class ReferenceMessage implements Parcelable {
    public JSONObject referenceJson = new JSONObject();
    private String messageId;
    private String name;
    private int msgType;
    private HTMessageBody body;



    protected ReferenceMessage(Parcel in) {
        messageId = in.readString();
        name = in.readString();
        msgType = in.readInt();
        String bodyString = in.readString();
        body = new HTMessageBody(bodyString);
    }

    public ReferenceMessage() {

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
        referenceJson.put("messageId",messageId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        referenceJson.put("name",name);
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
        referenceJson.put("msgType",msgType);
    }

    public HTMessageBody getBody() {
        return body;
    }

    public void setBody(HTMessageBody body) {
        this.body = body;
        referenceJson.put("body",JSONObject.parse(body.getXmppBody()));
    }

    public static final Creator<ReferenceMessage> CREATOR = new Creator<ReferenceMessage>() {
        @Override
        public ReferenceMessage createFromParcel(Parcel in) {
            return new ReferenceMessage(in);
        }

        @Override
        public ReferenceMessage[] newArray(int size) {
            return new ReferenceMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(name);
        dest.writeInt(msgType);
        dest.writeString(body.getLocalBody());
    }


    //用于通讯时,文件消息的本地地址不应该传输
    public String getXmppReference(){
        JSONObject json=new JSONObject();
        json.putAll(referenceJson);
        return json.toJSONString();
    }
}
