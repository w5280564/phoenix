package com.fastchat.sdk.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.client.HTAction;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.CallMessage;
import com.fastchat.sdk.model.CmdMessage;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageFileBody;
import com.fastchat.sdk.model.HTMessageImageBody;
import com.fastchat.sdk.model.HTMessageLocationBody;
import com.fastchat.sdk.model.HTMessageVideoBody;
import com.fastchat.sdk.model.HTMessageVoiceBody;
import com.fastchat.sdk.service.MessageService;
import com.fastchat.sdk.utils.Logger;
import com.fastchat.sdk.utils.UploadFileUtils;
import com.fastchat.sdk.utils.UploadFileUtilsNew;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ouyang on 2017/2/13.
 * 
 */

public class HTChatManager {

    private Context context;
    private MyReceiver receiver;
    private Map<String, HTMessageCallBack> htMessageCallBackMap = new HashMap<>();
    private String baseOssUrl="";

    public HTChatManager(Context context) {

        this.context = context;
        if(SDKConstant.IS_LIMITLESS){
            baseOssUrl= HTPreferenceManager.getInstance().getOssBaseUrl();

        }else {
            baseOssUrl= SDKConstant.baseOssUrl;
        }
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HTAction.ACTION_RESULT_MESSAGE);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);


    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals(HTAction.ACTION_RESULT_MESSAGE)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msgId = intent.getStringExtra("data");
                        boolean result = intent.getBooleanExtra("result", true);
                        long timeStamp=intent.getLongExtra("time",0);

                        if (htMessageCallBackMap.containsKey(msgId)) {
                            if (result) {
                                HTMessage htMessage = HTClient.getInstance().messageManager().getMessage(msgId);
                                if(htMessage!=null){
                                    htMessage.setStatus(HTMessage.Status.SUCCESS);
                                    htMessage.setTime(timeStamp);
                                    HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                                }

                                htMessageCallBackMap.get(msgId).onSuccess(timeStamp);
                            } else {
                                htMessageCallBackMap.get(msgId).onFailure();
                            }


                        }
                    }
                }).start();
            }
        }
    }



    /**
     * 发送消息
     * @param htMessage
     * @param htMessageCallBack
     */
    public void sendMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBack.onProgress();
                htMessageCallBackMap.put(htMessage.getMsgId(), htMessageCallBack);
                Log.d("sendAMessage:","---->SDK  sendMessage---->"+htMessage.getMsgId());

//                if(htMessage.getChatType()== ChatType.groupChat&&!HTClient.getInstance().groupManager().getAllGroups().contains(htMessage.getTo())){
//                    htMessageCallBack.onFailure();
//                }
                if (htMessage.getType() == HTMessage.Type.TEXT) {
                    sendXmppMessage(htMessage);
                } else if (htMessage.getType() == HTMessage.Type.IMAGE) {
                    sendImageMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.VOICE) {
                    sendVoiceMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.VIDEO) {
                    sendVideoMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.LOCATION) {
                    sendLocationMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.FILE) {
                    sendFileMessage(htMessage, htMessageCallBack);

                }

            }
        }).start();


    }


    /**
     * 发送文件消息
     * @param htMessage
     * @param htMessageCallBack
     */
    private void sendFileMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
        new UploadFileUtils(context, htMessageFileBody.getFileName(), htMessageFileBody.getLocalPath()).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                htMessageFileBody.setRemotePath(baseOssUrl + htMessageFileBody.getFileName());
                htMessage.setBody(htMessageFileBody);
                // htMessage.setStatus(HTMessage.Status.SUCCESS);
                sendXmppMessage(htMessage);

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });
    }



    /**
     * 发送图片消息
     * @param htMessage
     * @param htMessageCallBack
     */
    private void sendImageMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
        String filePath = htMessageImageBody.getLocalPath();
        final String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        final String fileName=HTPreferenceManager.getInstance().getUser().getUsername()+ System.currentTimeMillis()+suffix;
        File uploadFile = new File(filePath);
        new UploadFileUtilsNew(uploadFile).asyncUploadFile(new UploadFileUtilsNew.UploadCallBack() {

            @Override
            public void onSuccess(String remotePath) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        htMessageImageBody.setRemotePath(remotePath);
                        htMessage.setBody(htMessageImageBody);
                        sendXmppMessage(htMessage);
                    }
                }).start();
            }

            @Override
            public void onFailure(String description) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });

     }


    /**
     * 发送音频消息
     * @param htMessage
     * @param htMessageCallBack
     */
    private void sendVoiceMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
        String filePath = htMessageVoiceBody.getLocalPath();
        File uploadFile = new File(filePath);
        new UploadFileUtilsNew(uploadFile).asyncUploadFile(new UploadFileUtilsNew.UploadCallBack() {

            @Override
            public void onSuccess(String remotePath) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        htMessageVoiceBody.setRemotePath(remotePath);
                        htMessage.setBody(htMessageVoiceBody);
                        sendXmppMessage(htMessage);
                    }
                }).start();
            }

            @Override
            public void onFailure(String description) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });
    }


    /**
     * 发送视频消息
     * @param htMessage
     * @param htMessageCallBack
     */
    private void sendVideoMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {

        final HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();
        String thumbPath = htMessageVideoBody.getLocalPathThumbnail();
        final String filePath = htMessageVideoBody.getLocalPath();
        final String fileNameThumbnail = thumbPath.substring(thumbPath.lastIndexOf("/") + 1);
        File uploadFile = new File(filePath);
        new UploadFileUtilsNew(uploadFile).asyncUploadFile(new UploadFileUtilsNew.UploadCallBack() {

            @Override
            public void onSuccess(String remotePath) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        htMessageVideoBody.setRemotePath(remotePath);
                        htMessageVideoBody.setThumbnailRemotePath(remotePath);
                        htMessage.setBody(htMessageVideoBody);
                        htMessage.setStatus(HTMessage.Status.SUCCESS);
                        sendXmppMessage(htMessage);
                    }
                }).start();
            }

            @Override
            public void onFailure(String description) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });
    }


    /**
     * 发送位置消息
     * @param htMessage
     * @param htMessageCallBack
     */
    private void sendLocationMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();

        new UploadFileUtils(context, htMessageLocationBody.getFileName(), htMessageLocationBody.getLocalPath()).asyncUploadFile(new UploadFileUtils.UploadCallBack() {

            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                //  body.put(MessageUtils.REMOTE_PATH, htMessage.);
                //  body.put(MessageUtils.REMOTE_PATH_THUMBNAIL, baseOssUrl + fileName + MessageUtils.reSize);
                htMessageLocationBody.setRemotePath(baseOssUrl + htMessageLocationBody.getFileName());
                htMessage.setBody(htMessageLocationBody);
                sendXmppMessage(htMessage);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();

            }
        });

    }

    /**
     * 发送XmppMessage（最终调用）
     */
    private void sendXmppMessage(HTMessage htMessage) {
        Intent intent = new Intent(context, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_CHAT);
        intent.putExtra("chatTo", htMessage.getTo());
        intent.putExtra("body", htMessage.toXmppMessageBody());
        intent.putExtra("chatType", htMessage.getChatType().ordinal() + 1);
        intent.putExtra("msgId", htMessage.getMsgId());
        context. startService(intent);
        HTClient.getInstance().messageManager().saveMessage(htMessage, false);

    }

    /**
     * 发送透传消息
     * @param cmdMessage
     * @param htMessageCallBack
     */
    public void sendCmdMessage(final CmdMessage cmdMessage, final HTMessageCallBack htMessageCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBackMap.put(cmdMessage.getMsgId(), htMessageCallBack);
                htMessageCallBack.onProgress();
                 Intent intent = new Intent(context, MessageService.class);
                intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
                intent.putExtra("chatTo", cmdMessage.getTo());
                intent.putExtra("body", cmdMessage.toXmppMessage());
                intent.putExtra("msgId", cmdMessage.getMsgId());
                intent.putExtra("chatType", cmdMessage.getChatType().ordinal() + 1);
                context. startService(intent);
            }
        }).start();

    }

    public void sendCallMessage(final CallMessage callMessage, final HTMessageCallBack htMessageCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBackMap.put(callMessage.getMsgId(), htMessageCallBack);
                htMessageCallBack.onProgress();
                 Intent intent = new Intent(context, MessageService.class);
                //Intent intent = new Intent();
                intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
                intent.putExtra("chatTo", callMessage.getTo());
                intent.putExtra("body", callMessage.toXmppMessage());
                intent.putExtra("msgId", callMessage.getMsgId());
                intent.putExtra("chatType", callMessage.getChatType().ordinal() + 1);

               // intent.setAction(MessageService2.ACTIONN_SEND_MESSAGE_CMD);
                // context.startService(intent);
            //    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               // context.startService(intent);
              //  new Intent(MessageService2.this, MessageService2.class)

                context. startService(intent);


            }
        }).start();

    }

    public interface HTMessageCallBack {

        void onProgress();

        void onSuccess(long timeStamp);

        void onFailure();
    }


}
