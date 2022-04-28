package com.fastchat.sdk.utils;

import android.content.Context;
import android.content.Intent;

import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.CmdMessage;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.service.MessageService;

/**
 * Created by ouyang on 2017/1/17.
 * 
 */

public class HTMessageHelper {


    public static void sendHTMessage(final HTMessage htMessage, final Context context) {

                HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                Intent intent = new Intent(context, MessageService.class);
                intent.putExtra("TYPE", MessageService.TYPE_CHAT);
                intent.putExtra("chatTo", htMessage.getTo());
                intent.putExtra("body", htMessage.toXmppMessageBody());
                 int chatTypeInt=1;
                if(htMessage.getChatType()==ChatType.groupChat){
                    chatTypeInt=2;
                }
                intent.putExtra("chatType", chatTypeInt);
                intent.putExtra("msgId", htMessage.getMsgId());
               // context.startService(intent);

        context.startService(intent);



    }

    public static void sendCustomMessage(CmdMessage customMessage, Context context){

        Intent intent = new Intent(context, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
        intent.putExtra("chatTo", customMessage.getTo());
        intent.putExtra("body", customMessage.toXmppMessage());
        int chatTypeInt=1;
        if(customMessage.getChatType()==ChatType.groupChat){

            chatTypeInt=2;
        }
        intent.putExtra("chatType", chatTypeInt);
        intent.putExtra("msgId", customMessage.getMsgId());
        context.startService(intent);

    }



}
