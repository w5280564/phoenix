package com.fastchat.sdk.model;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

/**
 * @Description: SendCallbackMessageInfo
 * @Author: ouyang
 * @CreateDate: 2022/3/19 0019
 */
public class SendCallbackMessageInfo implements ExtensionElement {

    public static final String NAME_SPACE = "com.xml.extension";
    public static final String ELEMENT_NAME = "SendCallbackMessageInfo";

    private String msgId;

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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
        return "<received xmlns = 'urn:xmpp:receipts' id='"
                + msgId + "'/>";//新版本根标签不需要message 一定要加msgId
    }
}
