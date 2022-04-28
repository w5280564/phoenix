package com.fastchat.sdk.model;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.SDKConstant;

/**
 * Created by ouyang on 2017/4/9.
 * 
 */

public class CallMessage extends CmdMessage {

    @Override
    public String toXmppMessage() {
        JSONObject dataJson = new JSONObject();

        dataJson.put("from", getFrom());
        dataJson.put("to", getTo());
        dataJson.put("msgId", getMsgId());
        int chatTypeInt = 1;
        if (getChatType() == ChatType.groupChat) {
            chatTypeInt = 2;
        }
        dataJson.put("chatType", chatTypeInt);
        dataJson.put("body", getBody());
        JSONObject xmppJson = new JSONObject();
        xmppJson.put(SDKConstant.FX_MSG_KEY_TYPE, SDKConstant.TYPE_CALL);
        xmppJson.put(SDKConstant.FX_MSG_KEY_DATA, dataJson);

        return xmppJson.toJSONString();
    }
    public CallMessage(JSONObject bodyJson, long time) {
      super(bodyJson,time);
    }

    public CallMessage(){
        super();
    }


}
