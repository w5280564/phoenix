package com.fastchat.sdk.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ProgressConnection;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.client.HTAction;
import com.fastchat.sdk.listener.HTConnectionListener;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.model.CurrentUser;
import com.fastchat.sdk.model.SendCallbackMessageInfo;
import com.fastchat.sdk.model.SendMessageInfo;
import com.fastchat.sdk.utils.Logger;
import com.fastchat.sdk.utils.MessageUtils;
import com.fastchat.sdk.utils.NetWorkUtil;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台消息服务
 */
public class MessageService extends Service {
    public static final String TAG_SEND = "SEND_LOG";//发消息
    public static final String TAG_SEND_OTHER = "SEND_OTHER";//发消息_不需要打印日志
    public static final String TAG_RECEIVE = "RECEIVE_LOG";//接收消息
    public static final String TAG_RECEIVE_OTHER = "RECEIVE_OTHER";//接收消息_不需要打印日志
    public static final String TAG_CONNECT = "MessageServiceCONNECT";//连接XMPP消息
    public static final String TAG_NETWORK = "NETWORK";//网络连接消息
    private static XMPPTCPConnection xmppConnection;

    private MyBinder myBinder;
    private MyServiceConnection myServiceConnection;
    private LocalBroadcastManager localBroadcastManager;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_CHAT = 4;
    public static final int TYPE_INIT = 5;
    public static final int TYPE_LOGOUT = 6;
    public static final int TYPE_CHAT_CMD = 7;
    public static final int TYPE_AWAKE = 8;
    public static final int TYPE_NTIFICATION = 9;
    public static final int TYPE_NTIFICATION_CANCEL = 10;
    public static final int TYPE_DISCONNECT_BACKGROUND = 11;//应用退到后台断开链接
    public static final int TYPE_CONNECT_FOREGROUND = 12;//应用来到前台重新链接

    private long firstLoginMsgTime;
    private String loginUsername;
    private String loginPassword;
    private boolean isLogining = false;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    private CurrentUser currentUser;//当前登录用户
    private StanzaListener stanzaListenerMessage;//接收消息监听器

    private Handler xmppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //登录成功
                    final XMPPTCPConnection xmppConnectionNew = (XMPPTCPConnection) msg.obj;
                    if(xmppConnectionNew==null){
                        return;
                    }

                    if (xmppConnection == null || xmppConnectionNew.getStreamId()==null|| !xmppConnectionNew.getStreamId().equals(xmppConnection.getStreamId())) {
                        if (xmppConnection != null) {
                            xmppConnection.removeAsyncStanzaListener(stanzaListenerMessage);
                            xmppConnection.removeConnectionListener(connectionListener);
                        }
                        xmppConnection = xmppConnectionNew;
                        // 获取离线信息
//                        getOfflineMessage();
                        addListener();
                        PresenceBuilder presenceBuilder = StanzaBuilder.buildPresence()
                                .ofType(Presence.Type.available)
                                .setMode(Presence.Mode.available);
                        Presence mPresence =  presenceBuilder.build();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    xmppConnection.sendStanza(mPresence);
                                    Logger.d(TAG_SEND_OTHER, "用户上线");
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    // }
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.postDelayed(runnable, 30000);
                    break;


                case 1003:
                    //断线重连
                    if (currentUser != null) {
                        login(currentUser.getUsername(), currentUser.getPassword(), true);
                    }
                    break;
            }
        }
    };


    /**
     * 获取离线消息
     * @return
     */
    private Map<String, List<HashMap<String, String>>> getOfflineMessage() {
        if (xmppConnection == null) {
            return null;
        }
        Logger.d(TAG_RECEIVE,"---获取离线消息---");
        Map<String, List<HashMap<String, String>>> offlineMsgs = null;
        try {
            OfflineMessageManager offlineManager = OfflineMessageManager.getInstanceFor(xmppConnection);
            List<Message> messageList = offlineManager.getMessages();

            int count = offlineManager.getMessageCount();
            if (count <= 0) {
                return null;
            }
            offlineMsgs = new HashMap<>();
            for (Message message : messageList) {
                // TODO 根据实际处理
            }
            offlineManager.deleteMessages();
        } catch (Exception e) {
            Logger.d(TAG_RECEIVE,"---getOfflineMessage---" + e.getMessage());
        }
        return offlineMsgs;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra("TYPE", 0);
            switch (type) {
                case TYPE_LOGIN:
                    Logger.d(TAG_CONNECT, "登录操作");

                    loginUsername = intent.getStringExtra("username");
                    loginPassword = intent.getStringExtra("password");
                    if (loginUsername != null && loginPassword != null) {
                        isLogining = true;
                        login(loginUsername, loginPassword, false);
                    } else {
                        sendLoginResult(false);
                    }
                    break;
                case TYPE_LOGOUT:
                    Logger.d(TAG_CONNECT, "登出操作");
                    logoutXmpp();
                    break;
                case TYPE_CHAT:
                    String chatTo = intent.getStringExtra("chatTo");
                    String body = intent.getStringExtra("body");
                    String msgId = intent.getStringExtra("msgId");
                    int chatType = intent.getIntExtra("chatType", 1);
                    if (chatTo != null && body != null) {
                        sendMessage(chatTo, body, msgId, chatType);
                    }

                    break;
                case TYPE_CHAT_CMD:
                    Logger.d(TAG_CONNECT, "CMD消息");
                    String chatTo1 = intent.getStringExtra("chatTo");
                    String body1 = intent.getStringExtra("body");
                    String msgId1 = intent.getStringExtra("msgId");
                    int chatType1 = intent.getIntExtra("chatType", 1);
                    if (chatTo1 != null && body1 != null) {
                        sendMessage(chatTo1, body1, msgId1, chatType1);
                    }
                    break;
                case TYPE_AWAKE:
                    Logger.d(TAG_CONNECT, "唤醒操作");
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.post(runnable);
                    break;
                case TYPE_NTIFICATION:
                    //  setForeground(startId);
                    break;
                case TYPE_NTIFICATION_CANCEL:
                    cancelNotification();
                    break;
                case TYPE_DISCONNECT_BACKGROUND://应用退到后台断开链接
                    Logger.d(TAG_CONNECT, "进入后台---->主动断开");
                    disconnectInBackground();
                    break;
                case TYPE_CONNECT_FOREGROUND://应用来到前台重新链接
                    Logger.d(TAG_CONNECT, "进入前台---->主动重连");
                    connectInForeground();
                    break;


            }

        }
        return START_STICKY;
    }


    /**
     * 应用退到后台断开链接
     */
    private void disconnectInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PresenceBuilder presenceBuilder = StanzaBuilder.buildPresence()
                        .ofType(Presence.Type.unavailable);
                Presence mPresence =  presenceBuilder.build();
                try {
                    pingResult = false;
                    timerHandler.removeCallbacks(runnable);
                    if (xmppConnection != null) {
                        xmppConnection.sendStanza(mPresence);
                        Logger.d(TAG_SEND_OTHER, "用户下线");
                        xmppConnection.removeConnectionListener(connectionListener);
                        xmppConnection.removeAsyncStanzaListener(stanzaListenerMessage);
                        if (xmppConnection.isConnected()) {
                            xmppConnection.disconnect();
                            xmppConnection.instantShutdown();
                            xmppConnection = null;
                        }
                    }
                    Logger.d(TAG_CONNECT, "主动断开成功");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            }
        }).start();
    }

    /**
     * 应用来到前台重新链接
     */
    private void connectInForeground() {
        if (currentUser != null) {
            login(currentUser.getUsername(), currentUser.getPassword(), true);
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {


        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
        HTPreferenceManager.init(MessageService.this.getApplication());
        currentUser = HTPreferenceManager.getInstance().getUser();
        if (currentUser != null) {
            login(currentUser.getUsername(), currentUser.getPassword(), true);
        }

        //   initConnection();
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Logger.d(TAG_NETWORK, "NetworkCallbackImpl--->网络可用");
            if (currentUser != null) {
                timerHandler.removeCallbacks(runnable);
                timerHandler.postDelayed(runnable, 5000);
            }
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Logger.d(TAG_NETWORK, "NetworkCallbackImpl--->onLosing");
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d(TAG_NETWORK, "NetworkCallbackImpl--->网络断开");
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            // Toast.makeText(getBaseContext(), "onCapabilitiesChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            //Toast.makeText(getBaseContext(), "onLinkPropertiesChanged", Toast.LENGTH_SHORT).show();
        }
    }

    //发送连续连接状态,至监听
    private void sendConnectionConState(boolean isConnected, int code) {
        Intent intent = new Intent(HTAction.ACTION_CONNECTION);
        intent.putExtra("state", isConnected);
        intent.putExtra("code", code);
        localBroadcastManager.sendBroadcast(intent);
    }



    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    private void login(final String username, final String password, boolean isAutoLogin) {
        if (isAutoLogin && xmppConnection != null && xmppConnection.isAuthenticated() && pingResult) {
            return;
        }

        final XMPPTCPConnection xmpptcpConnection = new XMPPTCPConnection(getConfig());
        xmpptcpConnection.addConnectionListener(connectionListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    xmpptcpConnection.connect().login(username,password, Resourcepart.from("Android"));
                    Logger.d(TAG_CONNECT,"登录成功login");
                    if (isLogining) {
                        sendLoginResult(true);
                    }
                } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                    e.printStackTrace();
                    if (isLogining) {
                        sendLoginResult(false);
                    }
                }
            }
        }).start();

    }


    //初始化一个连接
    private XMPPTCPConnectionConfiguration getConfig() {
        String HOST = "";
        if (SDKConstant.IS_LIMITLESS) {
            HOST = HTPreferenceManager.getInstance().getIMServer();
        } else {
            HOST = SDKConstant.HOST;
        }

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setHost(HOST); // Name of your Host
        builder.setPort(SDKConstant.PORT);
        try
        {
            builder.setXmppDomain(JidCreate.domainBareFrom(SDKConstant.SERVER_NAME));
        }
        catch (XmppStringprepException e)
        {
            e.printStackTrace();
        }
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        builder.setCompressionEnabled(false);
        builder.setConnectTimeout(30000);
        //设置离线状态
        builder.setSendPresence(false);
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);//收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
        TLSUtils.acceptAllCertificates(builder);
        TLSUtils.disableHostnameVerificationForTlsCertificates(builder);
        final Map<String, String> registeredSASLMechanisms = SASLAuthentication.getRegisterdSASLMechanisms();
        for(String mechanism:registeredSASLMechanisms.values())
        {
            SASLAuthentication.blacklistSASLMechanism(mechanism);
        }
        SASLAuthentication.unBlacklistSASLMechanism(SASLPlainMechanism.NAME);
        XMPPTCPConnectionConfiguration configuration = builder.build();
        return configuration;
    }



    //检查连接
    private synchronized void checkConnection() {

        if (!NetWorkUtil.isNetworkConnected(this)) {
            Logger.d(TAG_CONNECT, "checkConnection:" + "网络断开连接");
            return;
        }
        if (currentUser == null) {
            Logger.d(TAG_CONNECT, "checkConnection:" + "no user info");
            return;
        }

        if (xmppConnection == null) {
            if (currentUser != null) {
                login(currentUser.getUsername(), currentUser.getPassword(), true);
            }
            return;
        }

        pingTest(new CallBack() {
            @Override
            public void onSuccess() {
                pingResult = true;
                Logger.d(TAG_CONNECT, "checkConnection:" + "pingTest onSuccess");
            }

            @Override
            public void onFailure(String errorMessage) {
                pingResult = false;
                Logger.d(TAG_CONNECT, "checkConnection:" + "pingTest onFailure errorMessage: " + errorMessage);
                timerHandler.removeCallbacks(runnable);
                android.os.Message message = xmppHandler.obtainMessage();
                message.what = 1003;
                message.sendToTarget();


            }
        });


    }

    private boolean pingResult = true;

    private synchronized void pingTest(final CallBack callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                PingManager pingManager = PingManager.getInstanceFor(xmppConnection);
                try {
                    boolean isOk = false;
                    try {
                        isOk = pingManager.pingMyServer();
                        if (isOk) {
                            Logger.d(TAG_CONNECT, "pingTest():" + "pingMyServer: isOk");
                            callBack.onSuccess();
                        } else {
                            Logger.d(TAG_CONNECT, "pingTest():" + "pingMyServer: isFailure");
                            callBack.onFailure("1000");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } catch (SmackException.NotConnectedException e) {
                    callBack.onFailure(e.getMessage());
                }
            }
        }).start();
    }


    private Handler timerHandler = new Handler();
    private static final int TIME = 30000;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                checkConnection();
                timerHandler.postDelayed(this, TIME);
                Logger.d(TAG_CONNECT, "runnable:" + "to checkConnection() ");
            } catch (Exception e) {
                Logger.d(TAG_CONNECT, "runnable:" + "Exception:" + e.getMessage());
                e.printStackTrace();
            }
        }
    };



    private void addListener() {
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(xmppConnection);
        reconnectionManager.enableAutomaticReconnection();//开启重联机制
        xmppConnection.removeConnectionListener(connectionListener);
        xmppConnection.addConnectionListener(connectionListener);
        Roster roster = Roster.getInstanceFor(xmppConnection);
        roster.setRosterLoadedAtLogin(false);
        if (firstLoginMsgTime == 0) {
            firstLoginMsgTime = System.currentTimeMillis();
        }
        stanzaListenerMessage = new StanzaListener() {

            @Override
            public void processStanza(final Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                Logger.d(TAG_RECEIVE_OTHER,  "processStanza-->" + packet.toXML().toString());
                ExtensionElement typeExtensionElement = packet.getExtension("urn:xmpp:receipts");
                String msgType = MessageUtils.getMsgType(typeExtensionElement.toXML().toString());
                Logger.d(TAG_RECEIVE_OTHER,  "msgType-->" + msgType);
                String from_packet = packet.getFrom().toString();
                if (SDKConstant.FX_MSG_KEY_RECEIPTS_REQUEST.equals(msgType)&&!from_packet.equals("app.im") &&
                        !XmppStringUtils.parseBareJid(from_packet).equals(XmppStringUtils.parseBareJid(xmppConnection.getUser().toString()))) {//普通消息
                    handleReceiveNormalMessage((Message) packet);
                }else{//处理发送回执消息
                    handleSendReceiptMessage((Message) packet);
                }
            }
        };

        StanzaFilter packetFilter = new StanzaFilter() {

            @Override
            public boolean accept(Stanza stanza) {
                if (stanza instanceof Message) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean test(Stanza stanza) {
                return false;
            }
        };
        xmppConnection.removeAsyncStanzaListener(stanzaListenerMessage);
        xmppConnection.addAsyncStanzaListener(stanzaListenerMessage, packetFilter);
    }

    /**
     * 处理普通消息
     * @param message_request
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    private void handleReceiveNormalMessage(Message message_request) throws SmackException.NotConnectedException, InterruptedException {
        ExtensionElement timeExtensionElement = message_request.getExtension("urn:xmpp:delay");
        long timeStamp = System.currentTimeMillis();
        if (timeExtensionElement != null) {
            timeStamp = MessageUtils.getTimeStamp(timeExtensionElement.toXML().toString());
        }
        StanzaError MStanzaError = message_request.getError();
        if (MStanzaError!=null) {
            String msgId = message_request.getStanzaId();
            sendMessageResult(false,true, msgId,timeStamp);
            String descriptiveTex = MStanzaError.getDescriptiveText();
            if (!TextUtils.isEmpty(descriptiveTex)) {
                    try {
                        descriptiveTex= URLDecoder.decode(descriptiveTex,"utf-8");
                        JSONObject jsonObject = JSONObject.parseObject(descriptiveTex);
                        String errorMsg = jsonObject.getString(SDKConstant.FX_MSG_KEY_ERROR_MSG);
                        Logger.d(TAG_RECEIVE_OTHER,  "errorMsg>" + errorMsg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
            }
        }else{
            Logger.d(TAG_RECEIVE,"接收消息:msgId--->" + message_request.getStanzaId()  + "  msgType--->" + message_request.getType() +"  timeStamp--->" + timeStamp+ "  to--->" + message_request.getTo()+ "  from--->" + message_request.getFrom());
            long duration = System.currentTimeMillis() - firstLoginMsgTime;
            if ((duration / 1000) < 2) {//处理离线消息
                MessageUtils.handleReceiveMessage(message_request, MessageService.this, true);
            } else {    //直接处理
                MessageUtils.handleReceiveMessage(message_request, MessageService.this, false);
            }
            handleReceiveReceiptMessage(message_request);
        }
    }


    /**
     * 处理接收接收回执消息
     * @param packet
     */
    private void handleReceiveReceiptMessage(Message packet) throws SmackException.NotConnectedException, InterruptedException {
        if (packet.getType() == Message.Type.chat) {//发送好友消息已读回执
            handleReceiveReceiptMessageTypeChat(packet);
        }else if(packet.getType() == Message.Type.groupchat) {//发送群消息已读回执
            handleReceiveReceiptMessageTypeGroupChat(packet);
        }
    }

    private void handleReceiveReceiptMessageTypeChat(Message packet) throws SmackException.NotConnectedException, InterruptedException {
        SendCallbackMessageInfo mSendCallbackMessageInfo = new SendCallbackMessageInfo();
        String msgId = packet.getStanzaId();
        mSendCallbackMessageInfo.setMsgId(msgId);
        Message callbackMessage = null;
        try {
            callbackMessage = xmppConnection.getStanzaFactory().buildMessageStanza()
                    .to("app.im")
                    .ofType(Message.Type.chat)
                    .addExtension(mSendCallbackMessageInfo)
                    .build();
            callbackMessage.setStanzaId(msgId);
            Message finalCallbackMessage = callbackMessage;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        xmppConnection.sendStanza(finalCallbackMessage);
                        Logger.d(TAG_SEND_OTHER, "sendCallbackStanza_Chat-->" + finalCallbackMessage.toXML().toString());
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    private void handleReceiveReceiptMessageTypeGroupChat(Message packet) throws SmackException.NotConnectedException, InterruptedException {
        String temp_from = packet.getFrom().toString();
        String receipt_from = "";
        if (!temp_from.isEmpty()&&temp_from.contains(SDKConstant.SERVER_DOMAIN_MUC)) {
            String [] mTemp = temp_from.split(SDKConstant.SERVER_DOMAIN_MUC);
            receipt_from = mTemp[0]+SDKConstant.SERVER_DOMAIN_MUC;
        }

        SendCallbackMessageInfo mSendCallbackMessageInfo = new SendCallbackMessageInfo();
        String msgId = packet.getStanzaId();
        mSendCallbackMessageInfo.setMsgId(msgId);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("from", receipt_from+"/"+currentUser.getUsername());

        Message callbackMessage = null;
        try {
            callbackMessage = xmppConnection.getStanzaFactory().buildMessageStanza()
                    .to(receipt_from)
                    .setBody(bodyJson.toJSONString())
                    .ofType(Message.Type.groupchat)//新版本一定要加上这个配置
                    .addExtension(mSendCallbackMessageInfo)
                    .build();
            callbackMessage.setStanzaId(msgId);
            Message finalCallbackMessage = callbackMessage;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        xmppConnection.sendStanza(finalCallbackMessage);
                        Logger.d(TAG_SEND_OTHER, "sendCallbackStanza_GroupChat-->" + finalCallbackMessage.toXML().toString());
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }



    /**
     * 处理接收发送回执消息
     * @param message_request
     */
    private void handleSendReceiptMessage(Message message_request) {
        ExtensionElement timeExtensionElement = message_request.getExtension("urn:xmpp:delay");
        long timeStamp = System.currentTimeMillis();
        if (timeExtensionElement != null) {
            timeStamp = MessageUtils.getTimeStamp(timeExtensionElement.toXML().toString());
        }

        ExtensionElement msgExtensionElement = message_request.getExtension("urn:xmpp:receipts");
        String msgId = String.valueOf(System.currentTimeMillis());
        if (msgExtensionElement != null) {
            msgId = MessageUtils.getMsgId(msgExtensionElement.toXML().toString());
        }
        sendMessageResult(true,false, msgId,timeStamp);

        String messageBody = message_request.getBody();
        if (messageBody != null) {
            try {
                messageBody= URLDecoder.decode(messageBody,"utf-8");
                JSONObject jsonObject = JSONObject.parseObject(messageBody);
                String target = jsonObject.getString(SDKConstant.FX_MSG_KEY_TARGET);
                Logger.d(TAG_SEND,"发送消息:msgId--->" + msgId  + "  msgType--->" + message_request.getType() +"  timeStamp--->" + timeStamp+ "  to--->" + target+ "  from--->" + message_request.getTo());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 退出
     */
    private void logoutXmpp() {
        Logger.d(TAG_CONNECT, "logoutXmpp");
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = null;
                timerHandler.removeCallbacks(runnable);
                HTPreferenceManager.getInstance().logout();

                if (xmppConnection != null) {
                    xmppConnection.removeConnectionListener(connectionListener);
                    xmppConnection.removeAsyncStanzaListener(stanzaListenerMessage);

                    if (xmppConnection.isConnected()) {
                        xmppConnection.disconnect();
                        xmppConnection.instantShutdown();
                    }
                    xmppConnection = null;
                }
                sendLogoutResult(true);
                // stopSelf();

            }
        }).start();

    }


    /**
     * 发送消息
     * @param chatTo
     * @param body
     * @param msgId
     * @param chatType
     */
    private void sendMessage(final String chatTo, final String body, final String msgId, final int chatType) {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            sendMessageResult(false,false, msgId, 0);
            Logger.d(TAG_NETWORK, "sendMessage" + "网络断开");
            return;
        }
        if (xmppConnection != null && xmppConnection.isConnected() && xmppConnection.isAuthenticated()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendStanza(chatTo, body, msgId, chatType);
                }
            }).start();
        } else {
            sendMessageResult(false,false, msgId,0);
        }
    }


    private void sendStanza(final String chatTo, final String body, final String msgId, final int chatType) {
        final String chatToJid;
        if (chatType == 2) {
            chatToJid = XmppStringUtils.parseBareJid(chatTo + SDKConstant.SERVER_DOMAIN_MUC);
        } else {
            chatToJid = XmppStringUtils.parseBareJid(chatTo + SDKConstant.SERVER_DOMAIN);
        }

        SendMessageInfo mSendMessageInfo = new SendMessageInfo();
        mSendMessageInfo.setBody(body);

        Message newMessage = null;
        try {
            newMessage = xmppConnection.getStanzaFactory().buildMessageStanza()
                    .to(chatToJid)
                    .ofType(chatType==2?Message.Type.groupchat:Message.Type.chat)//新版本一定要加上这个配置
                    .addExtension(mSendMessageInfo)
                    .build();
            try {
                newMessage.setStanzaId(msgId);
                xmppConnection.sendStanza(newMessage);
                Logger.d(TAG_SEND_OTHER, "发送消息>>>"+newMessage.toXML().toString());
            } catch (SmackException.NotConnectedException e) {
                sendMessageResult(false,false, msgId,0);
                e.printStackTrace();
            } catch (InterruptedException e) {
                sendMessageResult(false, false,msgId, 0);
                e.printStackTrace();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }


    /**
     * 刷新消息
     * @param isSuccess 消息是否发送成功
     * @param isErrorFresh 是否是业务层的错误刷新（比如说群禁言，群封禁等）
     * @param msgId 消息id
     * @param timeStamp 时间戳
     */
    private void sendMessageResult(boolean isSuccess,boolean isErrorFresh,String msgId, long timeStamp) {
        Intent intent = new Intent();
        intent.setAction(HTAction.ACTION_RESULT_MESSAGE);
        intent.putExtra("time", timeStamp);
        intent.putExtra("data", msgId);
        intent.putExtra("result", isSuccess);
        localBroadcastManager.sendBroadcast(intent);
        if (!isSuccess) {
            checkConnection();
        }
    }

    private ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void connecting(XMPPConnection connection) {

        }

        @Override
        public void connected(XMPPConnection connection) {
            Logger.d(TAG_CONNECT, "connectionListener:连接成功");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            android.os.Message message = xmppHandler.obtainMessage();
            message.obj = connection;
            message.what = 1000;
            message.sendToTarget();
            Logger.d(TAG_CONNECT, "connectionListener:连接验证通过" + "--->authenticated--->" + connection.toString() + "--->resumed--->" + resumed);
            sendConnectionConState(true, HTConnectionListener.NUMORL);
        }

        @Override
        public void connectionClosed() {
            sendConnectionConState(false, HTConnectionListener.NUMORL);
            Logger.d(TAG_CONNECT, "connectionListener:连接断开");
            if (isLogining) {
                sendLoginResult(false);
            }


        }

        @Override
        public void connectionClosedOnError(Exception e) {
            if (e.getMessage().contains("conflict")) {//账号在其他设备上登录
//                sendConnectionConState(false, HTConnectionListener.CONFLICT);
            } else {
                sendConnectionConState(false, HTConnectionListener.NUMORL);
            }
            if (isLogining) {
                sendLoginResult(false);
            }
            Logger.d(TAG_CONNECT, "connectionListener:连接出错--->errorInfo---->" + e.getMessage());
        }
    };




    private void sendLoginResult(boolean success) {
        if (success) {
            android.os.Message message = handler.obtainMessage();
            message.obj = success;
            message.what = 1000;
            handler.sendMessage(message);
        } else {
            Intent intent = new Intent(HTAction.ACTION_LOGIN);
            intent.putExtra("state", success);
            localBroadcastManager.sendBroadcast(intent);
            isLogining = false;
        }


    }

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //登录成功
                    boolean success = (boolean) msg.obj;
                    if (isLogining) {
                        HTPreferenceManager.getInstance().setUser(loginUsername, loginPassword);
                        currentUser = HTPreferenceManager.getInstance().getUser();
                        loginUsername = null;
                        loginPassword = null;
                    }
                    // MainManager.initManagerList(getApplicationContext());

                    Intent intent = new Intent(HTAction.ACTION_LOGIN);
                    intent.putExtra("state", success);
                    localBroadcastManager.sendBroadcast(intent);
                    isLogining = false;


                    break;
            }

        }

    };


    private void sendLogoutResult(boolean success) {
        Intent intent = new Intent(HTAction.ACTION_LOGOUT);
        intent.putExtra("state", success);
        localBroadcastManager.sendBroadcast(intent);
    }


    interface CallBack {
        void onSuccess();

        void onFailure(String errorMessage);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        if (myServiceConnection != null) {
            try {
                unbindService(myServiceConnection);
            } catch ( IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }


        stopConnection();
        super.onDestroy();
    }


    private void stopConnection() {
        if (runnable != null) {
            timerHandler.removeCallbacks(runnable);
        }
        if (xmppConnection != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xmppConnection.disconnect();
                    xmppConnection.instantShutdown();
                    xmppConnection = null;
                }
            }).start();

        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Logger.d("MyServiceConnection", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.d("MyServiceConnection", "本地服务Local被干掉");
            if (HTPreferenceManager.getInstance().isDualProcess()) {

            }
        }

    }


    class MyBinder extends ProgressConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return  MessageService.class.getName();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        myBinder = new MyBinder();
        return myBinder;

    }


}