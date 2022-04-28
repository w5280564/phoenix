package com.zhengshuo.phoenix.common.manager;

import android.graphics.Bitmap;

import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.manager.MmvkManger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyang on 2019/8/17.
 * 
 */
public class ChatFileManager {


    private static ChatFileManager chatFileManager;
    private static List<HTMessage> htMessageList = new ArrayList<>();

    public static ChatFileManager get() {
        if (chatFileManager == null) {
            chatFileManager = new ChatFileManager();

        }
        return chatFileManager;

    }

    public List<HTMessage> getImageOrVideoMessage() {

        return htMessageList;

    }

    public void addImageOrVideoMessage(HTMessage htMessage) {
        if(!htMessageList.contains(htMessage)){
            htMessageList.add(htMessage);
        }
    }

    public void removeImageOrVideoMessage(HTMessage htMessage) {
        htMessageList.remove(htMessage);
    }

    public void clearImageOrVideoMessage() {
        htMessageList.clear();
    }

    public void setLocalPath(String msgId, String localPath, HTMessage.Type type) {
        if (localPath != null && new File(localPath).exists()) {
            if (type == HTMessage.Type.VIDEO) {
                MmvkManger.getInstance().putString(msgId + "_video", localPath);

            } else if (type == HTMessage.Type.IMAGE) {

                MmvkManger.getInstance().putString(msgId + "_image", localPath);

            } else if (type == HTMessage.Type.VOICE) {

                MmvkManger.getInstance().putString(msgId + "_voice", localPath);

            }
        }
    }

    public String getLocalPath(String msgId, HTMessage.Type type) {
        if (type == HTMessage.Type.VIDEO) {
            return MmvkManger.getInstance().getAsString(msgId + "_video");

        } else if (type == HTMessage.Type.IMAGE) {

            return MmvkManger.getInstance().getAsString(msgId + "_image");

        } else if (type == HTMessage.Type.VOICE) {

            return MmvkManger.getInstance().getAsString(msgId + "_voice");

        }


        return null;
    }

    public void setMsgImageBitmap(String msgId, Bitmap bitmap) {

        MmvkManger.getInstance().putBtimap(msgId + "_bitmap", bitmap);

    }

    public Bitmap getMsgImageBitmap(String msgId) {
        return MmvkManger.getInstance().getBitmap(msgId + "_bitmap");
    }


}
