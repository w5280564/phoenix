package com.fastchat.sdk.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.model.SendCallbackMessageInfo;
import com.fastchat.sdk.utils.Logger;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.client.HTAction;
import com.fastchat.sdk.listener.HTConnectionListener;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.model.CurrentUser;
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
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;

import static com.fastchat.sdk.utils.MessageUtils.getMsgId;
import static com.fastchat.sdk.utils.MessageUtils.getTimeStamp;

/**
 * @author huangfangyi
 * @since 2016-05-05
 */
public class MessageService2 extends Service {
    public static final String TAG_SEND = "SEND_LOG";//发消息
    public static final String TAG_SEND_OTHER = "SEND_OTHER";//发消息_不需要打印日志
    public static final String TAG_RECEIVE = "RECEIVE_LOG";//接收消息
    public static final String TAG_RECEIVE_OTHER = "RECEIVE_OTHER";//接收消息_不需要打印日志
    public static final String TAG_CONNECT = "MessageServiceCONNECT";//连接XMPP消息
    public static final String TAG_NETWORK = "NETWORK";//网络连接消息
    public static final String TAG_OTHER = "OTHER";//其他消息

    private static final String TAG = MessageService.class.getSimpleName().toString();
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

    private Handler xmppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //登录成功
                    final XMPPTCPConnection xmppConnectionNew = (XMPPTCPConnection) msg.obj;
                    Log.d("xmppConnectionNew-->",xmppConnectionNew.getHost());
//                    if (xmppConnection != null) {
//                        if (!xmppConnection.getStreamId().equals(xmppConnectionNew.getStreamId())) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    xmppConnection.disconnect();
//                                    xmppConnection=xmppConnectionNew;
//                                    android.os.Message message=xmppHandler.obtainMessage();
//                                    message.what=1001;
//                                    message.sendToTarget();
//                                }
//                            }).start();
//                        }
//
//                    } else {
                    if(xmppConnectionNew==null){
                        return;
                    }

                    if (xmppConnection == null || xmppConnectionNew.getStreamId()==null|| !xmppConnectionNew.getStreamId().equals(xmppConnection.getStreamId())) {
                        if (xmppConnection != null) {
                            xmppConnection.removeAsyncStanzaListener(stanzaListener);
                            xmppConnection.removeConnectionListener(connectionListener);
                        }
                        xmppConnection = xmppConnectionNew;
                        addLisenter(xmppConnection);
                    }

                    // }
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.postDelayed(runnable, 30000);
                    scheduleJob();
                    break;

//                case 1001:
//                     addLisenter(xmppConnection);
//                    break;

                case 1003:
                    //断线重连
                    if (currentUser != null) {
                        login(currentUser.getUsername(), currentUser.getPassword(), true);
                    }

                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        boolean result= bindService(new Intent(this, RemoteService.class), myServiceConnection, Context.BIND_IMPORTANT);
        //     Logger.d("result----->",String.valueOf(result));

        if (intent != null) {
            int type = intent.getIntExtra("TYPE", 0);
            Logger.d("type----->", type + "");
            switch (type) {
                case TYPE_INIT:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_INIT");
//                    if (currentUser != null && currentUser.getUsername() != null && currentUser.getPassword() != null ) {
//                        timerHandler.removeCallbacks(runnable);
//                        timerHandler.postDelayed(runnable,30000);
////                        connectXmpp();
//                     }

                    break;
                case TYPE_LOGIN:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_LOGIN");

                    loginUsername = intent.getStringExtra("username");
                    loginPassword = intent.getStringExtra("password");
                    if (loginUsername != null && loginPassword != null) {
                        isLogining = true;
                        login(loginUsername, loginPassword, false);
                    } else {
                        sendLoginResult(false);
                    }
                    break;
                case TYPE_REGISTER:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_REGISTER");
//                    String username1 = intent.getStringExtra("username");
//                    String password1 = intent.getStringExtra("password");
//                    if (username1 != null && password1 != null) {
//                        register(username1, password1);
//                    }

                    break;
                case TYPE_LOGOUT:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_LOGOUT");
                    logoutXmpp();
                    break;
//                case TYPE_CHECK:
//                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHECK");
//                    checkConnection();
//                   // timerHandler.post(runnable);
//                    break;
                case TYPE_CHAT:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHAT");
                    String chatTo = intent.getStringExtra("chatTo");
                    String body = intent.getStringExtra("body");
                    String msgId = intent.getStringExtra("msgId");
                    int chatType = intent.getIntExtra("chatType", 1);
                    if (chatTo != null && body != null) {
                        sendMessage(chatTo, body, msgId, chatType);
                    }

                    break;
                case TYPE_CHAT_CMD:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_CHAT_CMD");
                    String chatTo1 = intent.getStringExtra("chatTo");
                    String body1 = intent.getStringExtra("body");
                    String msgId1 = intent.getStringExtra("msgId");
                    int chatType1 = intent.getIntExtra("chatType", 1);
                    if (chatTo1 != null && body1 != null) {
                        sendMessage(chatTo1, body1, msgId1, chatType1);
                    }
                    break;
                case TYPE_AWAKE:
                    Logger.d(TAG, "onStartCommand:" + "TYPE_AWAKE");
                    timerHandler.removeCallbacks(runnable);
                    timerHandler.post(runnable);
////                    if(getApplicationContext()==null){
//                        this.getApplication().onCreate();
////                    }

                    break;
                case TYPE_NTIFICATION:
                    //  setForeground(startId);
                    break;
                case TYPE_NTIFICATION_CANCEL:
                    cancelNotification();
                    break;


            }

        }

        return START_STICKY;
    }


    int notifyId = 30;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        Logger.d("MessageService32", "onCreate()");


        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
        HTPreferenceManager.init(MessageService2.this.getApplication());
        currentUser = HTPreferenceManager.getInstance().getUser();
        if (currentUser != null && currentUser.getUsername() != null && currentUser.getPassword() != null) {

//                        connectXmpp();
            login(currentUser.getUsername(), currentUser.getPassword(), true);
        }

        //   initConnection();
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
        //  initBroadCastReceiver();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(1, new Notification());
//        }
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
            Logger.d(TAG, "NetworkCallbackImpl--->onAvailable");
            timerHandler.removeCallbacks(runnable);
            timerHandler.postDelayed(runnable, 5000);
            //  Toast.makeText(getBaseContext(), "onAvailable", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Logger.d(TAG, "NetworkCallbackImpl--->onLosing");

            //  Toast.makeText(getBaseContext(), "onLosing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d(TAG, "NetworkCallbackImpl--->onLost");
            //   Toast.makeText(getBaseContext(), "onLost", Toast.LENGTH_SHORT).show();
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

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (locale.getDefault().toString().contains("zh"))

            return true;
        else
            return false;
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

//    private void initConnection() {
//        Log.d(TAG, "initConnection");
//
//        if (xmppConnection != null) {
//            if (xmppConnection.isConnected() && xmppConnection.isAuthenticated() && pingResult) {
//                return;
//            }
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    xmppConnection.disconnect();
//                    if (!xmppConnection.isSocketClosed() && xmppConnection.isConnected()) {
//                        xmppConnection.instantShutdown();
//                        xmppConnection = null;
//                    }
//                }
//            }).start();
//        }
//        xmppConnection = new XMPPTCPConnection(getConfig());
//        Roster roster = Roster.getInstanceFor(xmppConnection);
//        roster.setRosterLoadedAtLogin(false);
//
//        xmppConnection.addConnectionListener(connectionListener);
//        addLisenter(xmppConnection);
//    }


    boolean isFinished = true;

//    private void connectXmpp() {
//        if (!isFinished) {
//            if (isLogining) {
//                sendLoginResult(false);
//            }
//            Logger.d(TAG, "connectXmpp" + "isFinished--->" + isFinished + "-->isLogining--->" + isLogining);
//
//            return;
//        }
//        if (!NetWorkUtil.isNetworkConnected(this)) {
//            Logger.d(TAG, "connectXmpp" + "isNetworkConnected is error");
//            if (isLogining) {
//                sendLoginResult(false);
//            }
//
//            return;
//        }
//
//        if (xmppConnection != null) {
//            isFinished = false;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                        xmppConnection.connect();
//                        Logger.d(TAG, "connectXmpp" + "-->connect()");
//                    } catch (SmackException | IOException | XMPPException e) {
//
//                        Logger.d(TAG, "connectXmpp" + "connect()--->e--->" + e.getMessage().toString());
//                        if ("Client is already connected".equals(e.getMessage().toString()) || "Client is already logged in".equals(e.getMessage().toString())) {
//                            if (isLogining) {
//                                login();
//                            }
//
//                        } else {
//                            if (isLogining) {
//                                sendLoginResult(false);
//                            }
//                        }
//                        e.printStackTrace();
//                    }
//                    isFinished = true;
//
//                }
//            }).start();
//
//        } else {
//            isFinished = true;
//            sendLoginResult(false);
//        }
//
//    }

    //初始化一个连接
    private XMPPTCPConnectionConfiguration getConfig() {
        String HOST = "";
        if (SDKConstant.IS_LIMITLESS) {
            HOST = HTPreferenceManager.getInstance().getIMServer();
        } else {
            HOST = SDKConstant.HOST;
        }

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(HOST)  // Name of your Host
                .setPort(SDKConstant.PORT);
        try
        {
            builder.setXmppDomain(JidCreate.domainBareFrom(SDKConstant.SERVER_NAME));
        }
        catch (XmppStringprepException e)
        {
            e.printStackTrace();
        }
        builder.setCompressionEnabled(false);
        builder.setConnectTimeout(30000);
        builder.setSendPresence(true);
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

//    //连接到服务器,1--仅仅连接不登录,给注册用的.
//    private void register(final String username, final String password) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (xmppConnection == null) {
//                        initConnection();
//                    }
//                    xmppConnection.connect();
//                    AccountManager accountManager = AccountManager.getInstance(xmppConnection);
//                    accountManager.sensitiveOperationOverInsecureConnection(true);
//                    accountManager.createAccount(username, password);
//                    xmppConnection.disconnect();
//                    sendRgisterResult(true);
//                } catch (SmackException | XMPPException | IOException e) {
//                    e.printStackTrace();
//                    Logger.d("e--->", e.getMessage().toString());
//                    if (e.getMessage().toString().equals("Client is already connected")) {
//                        AccountManager accountManager = AccountManager.getInstance(xmppConnection);
//                        accountManager.sensitiveOperationOverInsecureConnection(true);
//                        try {
//                            accountManager.createAccount(username, password);
//                            xmppConnection.disconnect();
//                            sendRgisterResult(true);
//                        } catch (SmackException.NoResponseException e1) {
//                            e1.printStackTrace();
//                            sendRgisterResult(false);
//                        } catch (XMPPException.XMPPErrorException e1) {
//                            e1.printStackTrace();
//                            sendRgisterResult(false);
//                        } catch (SmackException.NotConnectedException e1) {
//                            sendRgisterResult(false);
//                            e1.printStackTrace();
//                        }
//                        return;
//                    }
//                    sendRgisterResult(false);
//                }
//            }
//        }).start();
//    }


    //检查连接
    private synchronized void checkConnection() {

        if (!NetWorkUtil.isNetworkConnected(this)) {
            Logger.d(TAG, "checkConnection:" + "noNetworkConnected");
            return;
        }
        if (currentUser == null) {
            Logger.d(TAG, "checkConnection:" + "no user info");
            return;
        }

        if (xmppConnection == null) {
            login(currentUser.getUsername(), currentUser.getPassword(), true);

            return;
        }
        pingTest(new CallBack() {
            @Override
            public void onSuccess() {
                pingResult = true;


//                if (localBroadcastManager == null || broadcastReceiver == null) {
//                    initBroadCastReceiver();
//                    Logger.d(TAG, "checkConnection:broadcastReceiver error");
//
//                }


                //     sendState("ping  is success");
                Logger.d(TAG, "checkConnection:" + "pingTest onSuccess");
//                Logger.d(TAG, "checkConnection:isConnected" + String.valueOf(xmppConnection.isConnected()));
//                Logger.d(TAG, "checkConnection:isSocketClosed" + String.valueOf(xmppConnection.isSocketClosed()));
//                Logger.d(TAG, "checkConnection:isAuthenticated" + String.valueOf(xmppConnection.isAuthenticated()));
                // Logger.d(TAG, "checkConnection:" + String.valueOf(xmppConnection.isAuthenticated()));
            }

            @Override
            public void onFailure(String errorMessage) {
                pingResult = false;
                Logger.d(TAG, "checkConnection:" + "pingTest onFailure errorMessage: " + errorMessage);
//                if (errorMessage.equals("1000")) {
//                    if (xmppConnection != null) {
//                        xmppConnection.disconnect();
//                    }
//                }else {
//
//                }
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
                            Log.d(TAG, "pingTest():" + "pingMyServer: isOk");
                            callBack.onSuccess();
                        } else {
                            Log.d(TAG, "pingTest():" + "pingMyServer: isFailure");
                            callBack.onFailure("1000");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } catch (SmackException.NotConnectedException e) {
                    Log.d(TAG, "pingTest():" + "NotConnectedException: " + e.getMessage().toString());

                    callBack.onFailure("2000");
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
                Logger.d(TAG, "runnable:" + "to checkConnection() ");
            } catch (Exception e) {
                Logger.d(TAG, "runnable:" + "Exception:" + e.getMessage().toString());
                e.printStackTrace();
            }
        }
    };

    StanzaListener stanzaListener;

    private void addLisenter(final XMPPTCPConnection mXmpp) {

        Logger.d(TAG, "addLisenter:");
        ReconnectionManager.getInstanceFor(mXmpp).enableAutomaticReconnection();

        mXmpp.removeConnectionListener(connectionListener);
        mXmpp.addConnectionListener(connectionListener);
        Roster roster = Roster.getInstanceFor(mXmpp);
        roster.setRosterLoadedAtLogin(false);
        if (firstLoginMsgTime == 0) {
            firstLoginMsgTime = System.currentTimeMillis();
        }
        stanzaListener = new StanzaListener() {

            @Override
            public void processStanza(final Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                Logger.d(TAG_RECEIVE_OTHER,  "processStanza-->" + packet.toXML().toString());
                String from_packet = packet.getFrom().toString();
                if (!from_packet.equals("app.im") &&
                        !XmppStringUtils.parseBareJid(from_packet).equals(XmppStringUtils.parseBareJid(xmppConnection.getUser().toString())) &&
                        !from_packet.contains("tigase")) {//普通消息
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
        mXmpp.removeAsyncStanzaListener(stanzaListener);
        mXmpp.addAsyncStanzaListener(stanzaListener, packetFilter);
    }

    /**
     * 处理普通消息
     * @param packet
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    private void handleReceiveNormalMessage(Message packet) throws SmackException.NotConnectedException, InterruptedException {
        Message message_request = packet;
        ExtensionElement timeExtensionElement = message_request.getExtension("urn:xmpp:delay");
        long timeStamp = System.currentTimeMillis();
        if (timeExtensionElement != null) {
            timeStamp = getTimeStamp(timeExtensionElement.toXML().toString());
        }
        Logger.d(TAG_RECEIVE,"接收消息:msgId--->" + message_request.getStanzaId()  + "  msgType--->" + message_request.getType() +"  timeStamp--->" + timeStamp+ "  to--->" + message_request.getTo()+ "  from--->" + message_request.getFrom());
        long duration = System.currentTimeMillis() - firstLoginMsgTime;
        if ((duration / 1000) < 2) {//处理离线消息
            MessageUtils.handleReceiveMessage(packet, MessageService2.this, true);
        } else {    //直接处理
            MessageUtils.handleReceiveMessage(packet, MessageService2.this, false);
        }
        handleReceiveReceiptMessage(message_request);
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
     * @param packet
     */
    private void handleSendReceiptMessage(Message packet) {
        Message message_request = packet;
        ExtensionElement timeExtensionElement = message_request.getExtension("urn:xmpp:delay");
        long timeStamp = System.currentTimeMillis();
        if (timeExtensionElement != null) {
            timeStamp = getTimeStamp(timeExtensionElement.toXML().toString());
        }

        ExtensionElement msgExtensionElement = message_request.getExtension("urn:xmpp:receipts");
        String msgId = String.valueOf(System.currentTimeMillis());
        if (msgExtensionElement != null) {
            msgId = getMsgId(msgExtensionElement.toXML().toString());
        }
        sendMessageResult(true, msgId,timeStamp);

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



    private CurrentUser currentUser;


    private void logoutXmpp() {
        Logger.d(TAG, "logoutXmpp");

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = null;
                timerHandler.removeCallbacks(runnable);
                HTPreferenceManager.getInstance().logout();

                if (xmppConnection != null) {
                    xmppConnection.removeConnectionListener(connectionListener);
                    xmppConnection.removeAsyncStanzaListener(stanzaListener);

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
            sendMessageResult(false, msgId, 0);
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
            sendMessageResult(false, msgId,0);
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
            } catch (SmackException.NotConnectedException e) {
                sendMessageResult(false, msgId,0);
                e.printStackTrace();
            } catch (InterruptedException e) {
                sendMessageResult(false, msgId, 0);
                e.printStackTrace();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }



    private void sendMessageResult(boolean success, String msgId, long timeStamp) {

        Intent intent = new Intent();
        intent.putExtra("time", timeStamp);
        intent.putExtra("data", msgId);
        intent.putExtra("result", success);
        localBroadcastManager.sendBroadcast(intent);

        if (!success) {
            checkConnection();
        }
    }

    private ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void connecting(XMPPConnection connection) {

        }

        @Override
        public void connected(XMPPConnection connection) {
            Logger.d(TAG_CONNECT, "connectionListener:" + "--->连接成功");
//            if(xmppConnection!=null){
//
//
//                Logger.d(TAG,"CURRENT_XMPP"+xmppConnection.getStreamId());
//                Logger.d(TAG,"CURRENT_XMPP1"+connection.getStreamId());
//            }else {
//                Logger.d(TAG,"CURRENT_XMPP2"+connection.getStreamId());
//             }
//            //如果是登录页面执行登陆操作
//            login();
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            android.os.Message message = xmppHandler.obtainMessage();
            message.obj = connection;
            message.what = 1000;
            message.sendToTarget();
            Logger.d(TAG_CONNECT, "connectionListener:" + "--->authenticated通过--->" + connection.toString() + "--->resumed--->" + resumed);
            sendConnectionConState(true, 0);
            timerHandler.removeCallbacks(runnable);
            timerHandler.post(runnable);
            if (isLogining) {
                sendLoginResult(true);
            }


        }

        @Override
        public void connectionClosed() {
            sendConnectionConState(false, 0);
            Logger.d(TAG_CONNECT, "connectionListener:" + "--->connectionClosed--->连接断开");
            if (isLogining) {
                sendLoginResult(false);
            }


        }

        @Override
        public void connectionClosedOnError(Exception e) {
            if (e.getMessage().contains("<stream:error><conflict xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>")) {

//                //如果有登录用户信息,并考虑自动重连机制
//                if (currentUser != null) {
//                    checkDeviceId(true);
//
//                }
//                sendConnectionConState(false, HTConnectionListener.CONFLICT);
                //   logoutXmpp();

            } else {
                sendConnectionConState(false, HTConnectionListener.NUMORL);
            }
            if (isLogining) {
                sendLoginResult(false);
            }

            Logger.d(TAG_CONNECT, "connectionListener:" + "--->connectionClosedOnError--->连接出错---->" + e.getMessage().toString());


        }

//        @Override
//        public void reconnectionSuccessful() {
//            sendConnectionConState(true, 1);
//            Logger.d(TAG, "connectionListener:" + "--->reconnectionSuccessful--->");
//        }
//
//        @Override
//        public void reconnectingIn(int seconds) {
//
//        }
//
//        @Override
//        public void reconnectionFailed(Exception e) {
//            sendConnectionConState(false, HTConnectionListener.RECONNECT_ERROR);
//            //   sendState("reconnectionFailed" + e.getMessage());
//            Logger.d(TAG, "connectionListener:" + "--->reconnectionFailed--->e--->" + e.getMessage().toString());
//        }
    };
//
//    private void login() {
//
//        if (!TextUtils.isEmpty(loginPassword) && !TextUtils.isEmpty(loginUsername)) {
//            try {
//                xmppConnection.login(loginUsername, loginPassword);
//                //登录后,启动自动断线重连
//                //reconnectionManager.enableAutomaticReconnection();
//                if (isLogining) {
//                    sendLoginResult(true);
//
//                }
//            } catch (XMPPException | SmackException | IOException e) {
//                Logger.d("e--->", e.getMessage().toString());
//                if (e.getMessage().toString().equals("Client is already logged in")) {
//                    if (isLogining) {
//                        sendLoginResult(true);
//
//                    }
//                    return;
//                }
//                if (isLogining) {
//                    Logger.d("false---->", "4444");
//
//                    sendLoginResult(false);
//                }
//                Logger.d("login--->", e.getMessage().toString());
//                stopSelf();
//                e.printStackTrace();
//            }
//
//            return;
//        } else {
//
//            //如果有登录用户信息,并考虑自动重连机制
//            if (currentUser != null) {
//                try {
//                    xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                    //登录后,启动自动断线重连
//                    //  reconnectionManager.enableAutomaticReconnection();
//                } catch (XMPPException | SmackException | IOException e) {
//                    if (e.getMessage().toString().equals("Client is already logged in")) {
//                        return;
//                    }
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//        scheduleJob();
//
//    }

//
//    private void checkDeviceId(boolean isConflict) {
//
//
//        new HTHttpUtils(this).getDeviceId(currentUser.getUsername(), new HTHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                if (jsonObject != null) {
//                    int code = jsonObject.getInteger("code");
//                    if (code == 1) {
//                        String deviceIdServer = jsonObject.getString("deviceId");
//                        if (!TextUtils.isEmpty(deviceIdServer) && !TextUtils.isEmpty(DEVICE_ID) && !DEVICE_ID.equals(deviceIdServer)) {
//                            Log.d(TAG, "deviceIdServer---"+deviceIdServer+"----"+DEVICE_ID);
//                            sendConnectionConState(false, HTConnectionListener.CONFLICT);
//                           Intent intent=  new Intent(MessageService32.this, MessageService32.class).putExtra("TYPE", TYPE_LOGOUT);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                 startForegroundService(intent);
//                            } else {
//                               startService(intent);
//                            }
//
//
//                            return;
//                        }
//                    }
//                    try {
//                        xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                        //登录后,启动自动断线重连
//                        //  reconnectionManager.enableAutomaticReconnection();
//                    } catch (XMPPException | SmackException | IOException e) {
//                        if (e.getMessage().toString().equals("Client is already logged in")) {
//                            return;
//                        }
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d(TAG, "login getDeviceId json is null");
//                    try {
//                        xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                        //登录后,启动自动断线重连
//                        //  reconnectionManager.enableAutomaticReconnection();
//                    } catch (XMPPException | SmackException | IOException e) {
//                        if (e.getMessage().toString().equals("Client is already logged in")) {
//                            return;
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//                Log.d(TAG, "login getDeviceId error"+SDKConstant.DEVICE_URL_GET);
//                try {
//                    xmppConnection.login(currentUser.getUsername(), currentUser.getPassword());
//                    //登录后,启动自动断线重连
//                    //  reconnectionManager.enableAutomaticReconnection();
//                } catch (XMPPException | SmackException | IOException e) {
//                    if (e.getMessage().toString().equals("Client is already logged in")) {
//                        return;
//                    }
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }

    /**
     * Executed when user clicks on SCHEDULE JOB.
     */
    private void scheduleJob() {
        Logger.d("KeepAliveService", "scheduleJob");
        if (HTPreferenceManager.getInstance().isDualProcess()) {
        }
    }


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
              /*  case 2000:
                    //群消息离线接收完之后，向服务器告知最近的推送时间戳
                    timer.schedule(task,3000);
*/

            }

        }

    };

    private void sendRgisterResult(boolean success) {
        Intent intent = new Intent(HTAction.ACTION_REGISTER);
        intent.putExtra("data", success);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendLogoutResult(boolean success) {
        Intent intent = new Intent(HTAction.ACTION_LOGOUT);
        //  intent.putExtra("TYPE", ReceiverConstant.TYPE_LOGOUT_RESULT);
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

//        if(localBroadcastManager!=null ){
//            localBroadcastManager.unregisterReceiver(broadcastReceiver);
//        }


        stopConnection();

        stopForeground(true);
        //  HTClient.getInstance().destroy();
        // getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
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
                }
            }).start();

        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Logger.d("castiel", "本地服务连接成功");
//            ProgressConnection iMyAidlInterface = ProgressConnection.Stub.asInterface(arg1);
//            try {
//                Log.i("LocalService", "connected with " + iMyAidlInterface.getProName());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {


            // 连接出现了异常断开了，LocalCastielService被杀死了
            Logger.d("two service---->", "本地服务Local被干掉");
        }

    }

//    class MyBinder extends ProgressConnection.Stub {
//
//        @Override
//        public String getProName() throws RemoteException {
//            return  MessageService.class.getName();
//        }
//
//    }


    class MyBinder extends Binder {
        public String getProName(){
            return  MessageService.class.getName();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        myBinder = new MyBinder();
        return myBinder;

    }


}