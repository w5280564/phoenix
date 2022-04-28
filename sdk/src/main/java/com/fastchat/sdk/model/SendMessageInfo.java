package com.fastchat.sdk.model;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

public class SendMessageInfo implements ExtensionElement {
    public static final String NAME_SPACE = "urn:xmpp:receipts";
    //用户信息元素名称
    public static final String ELEMENT_NAME = "userinfo";
    private String body;




    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public CharSequence toXML(XmlEnvironment xmlEnvironment) {
        return "<body>" + body+ "</body><request xmlns='urn:xmpp:receipts'/>";
    }
}
