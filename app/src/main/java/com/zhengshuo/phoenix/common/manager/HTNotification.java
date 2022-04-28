package com.zhengshuo.phoenix.common.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.core.app.NotificationCompat;

import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageTextBody;
import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;

/**
 * Created by ouyang on 2016/12/19.
 * 
 */

public class HTNotification {
    private NotificationManager manager = null;
    private Context mContext;
    private NotificationCompat.Builder builder;

    public HTNotification(Context context) {
        this.mContext = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //为了版本兼容  选择V4包下的NotificationCompat进行构造
        builder = new NotificationCompat.Builder(mContext);
    }

    private Notification notification;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
//                    BadgeUtil.setBadgeCount(mContext, HTClientHelper.BadgerCount,R.drawable.app_logo_ql,notification);
                    //todo
                    break;
            }
        }
    };

    public void onNewMessage(HTMessage htMessage) {
//        String userId = htMessage.getUsername();
//        String title = htMessage.getAttributes().getString("nick");
//        Intent intent = new Intent();
//        intent.setClass(mContext, ChatActivity.class);
//        intent.putExtra("conversationId", userId);
//        if (htMessage.getChatType() == ChatType.singleChat) {
//            String nick = UserManager.get().getUserNick(userId);
//            title = nick;
//            intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
//        } else if (htMessage.getChatType() == ChatType.groupChat) {
//            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
//            if (htGroup != null) {
//                title = htGroup.getGroupName();
//            }
//            intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
//        }
//        String content = getContent(htMessage);
//
//        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.parseInt(userId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(mContext.getApplicationInfo().icon);
//        notification = builder.build();
//        /**
//         * 判断是否打开了某些设置
//         * */
//        if (SettingsManager.getInstance().getSettingMsgNotification()) {
//            if (SettingsManager.getInstance().getSettingMsgSound() && !SettingsManager.getInstance().getSettingMsgVibrate()) {
//                notification.flags = Notification.DEFAULT_SOUND;
//            } else if (SettingsManager.getInstance().getSettingMsgVibrate() && !SettingsManager.getInstance().getSettingMsgSound()) {
//                notification.flags = Notification.DEFAULT_VIBRATE;
//            } else if (SettingsManager.getInstance().getSettingMsgVibrate() && SettingsManager.getInstance().getSettingMsgSound()) {
//                notification.flags = Notification.DEFAULT_ALL;
//            } else {
//                notification.flags = Notification.DEFAULT_LIGHTS;
//            }
//
//
//        }
//        notification.icon = mContext.getApplicationInfo().icon;
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//
//        String id = "channel_01";
//        String name = "Fast IM";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
//            manager.createNotificationChannel(mChannel);
//            notification = new Notification.Builder(mContext)
//                    .setChannelId(id)
//                    .setContentTitle(title)
//                    .setContentText(content)
//                    .setContentIntent(pendingIntent)
//                    .setSmallIcon(mContext.getApplicationInfo().icon).build();
//            Message message = handler.obtainMessage();
//            message.what = 1000;
//            message.sendToTarget();
////            try {
////
////                Field field = notification.getClass().getDeclaredField("extraNotification");
////
////                Object extraNotification = field.get(notification);
////
////                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
////
////                method.invoke(extraNotification, HTClientHelper.BadgerCount);
////
////            } catch (Exception e) {
////
////                e.printStackTrace();
////
////            }
//
//
////            notification.setChannelId(id)
////                    .setContentTitle("5 new messages")
////                    .setContentText("hahaha")
////                    .setSmallIcon(R.mipmap.error_image_placeholder).build();
//        }
//
//
//        manager.notify(Integer.parseInt(userId), notification);//发送通知
    }

    public void cancel(int id) {
        manager.cancel(id);
    }

    protected final static int[] msgs = {R.string.sent_a_message, R.string.sent_a_picture, R.string.sent_a_voice, R.string.sent_a_location, R.string.sent_a_video, R.string.sent_a_file,
            R.string.contacts_messages};

    private String getContent(HTMessage message) {
        HTConversation htConversation = HTClient.getInstance().conversationManager().getConversation(message.getFrom());
        String notifyText = "";
        if (htConversation != null) {
            if (htConversation.getUnReadCount() > 0) {
                notifyText = mContext.getString(R.string.zhongkuohao) + htConversation.getUnReadCount() + mContext.getString(R.string.zhongkuohao_msg);

            }

        }

        switch (message.getType()) {
            case TEXT:
                HTMessageTextBody htMessageTextBody = (HTMessageTextBody) message.getBody();
                String content = htMessageTextBody.getContent();
                if (content != null) {
                    notifyText += content;
                } else {
                    notifyText += getString(msgs[0]);
                }
                break;
            case IMAGE:
                notifyText += getString(msgs[1]);
                break;
            case VOICE:
                notifyText += getString(msgs[2]);
                break;
            case LOCATION:
                notifyText += getString(msgs[3]);
                break;
            case VIDEO:
                notifyText += getString(msgs[4]);
                break;
            case FILE:
                notifyText += getString(msgs[4]);
                break;
        }
        return notifyText;
    }

    private String getString(int res) {
        if (mContext != null) {
            return mContext.getString(res);
        }
        return "";
    }
}
