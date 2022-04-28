package com.fastchat.sdk.model;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.utils.JsonUtil;

/**
 * Created by ouyang on 2017/2/14.
 * 
 */

public class HTMessageTextBody extends HTMessageBody {
    private String content;
    private ReferenceMessage reference;
    public HTMessageTextBody(JSONObject bodyJson) {
        super(bodyJson);
     }
    public HTMessageTextBody(String body) {
       super(body);
    }

    public HTMessageTextBody(){

    }

    public void setContent(String content){
        this.content=content;
        bodyJson.put("content",content);
    }

    public String getContent() {
        if(content==null){
            content=bodyJson.getString("content");
        }

        return content;
    }

    public ReferenceMessage getReference() {
        if(reference==null){
            JSONObject jsonObject = (JSONObject) bodyJson.get("reference");
            reference = JSONObject.toJavaObject(jsonObject, ReferenceMessage.class);
        }
        return reference;
    }

    public void setReference(ReferenceMessage reference) {
        this.reference = reference;
        bodyJson.put("reference",JSONObject.parse(reference.getXmppReference()));
    }
}
