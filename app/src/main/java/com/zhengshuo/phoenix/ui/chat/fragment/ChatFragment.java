package com.zhengshuo.phoenix.ui.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.manager.SettingsManager;
import com.zhengshuo.phoenix.ui.chat.activity.FastGaodeMapActivity;
import com.zhengshuo.phoenix.ui.chat.weight.FastChatRowVoicePlayer;
import com.zhengshuo.phoenix.ui.chat.weight.FastVoiceRecorderView;
import com.zhengshuo.phoenix.ui.chat.weight.VoicePlayClickListener;
import com.zhengshuo.phoenix.ui.chat.weight.emojicon.SmileUtils;
import com.zhengshuo.phoenix.ui.dialog.InputStringDialog;
import com.zhengshuo.phoenix.util.LinkifySpannableUtils;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.widget.menu.FastPopupWindow;
import com.zhengshuo.phoenix.widget.menu.MenuItemBean;
import com.zhengshuo.phoenix.widget.menu.PopupWindowHelper;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.HTChatManager;
import com.fastchat.sdk.model.CmdMessage;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageBody;
import com.fastchat.sdk.model.HTMessageImageBody;
import com.fastchat.sdk.model.HTMessageTextBody;
import com.fastchat.sdk.model.HTMessageVideoBody;
import com.fastchat.sdk.model.ReferenceMessage;
import com.fastchat.sdk.utils.Logger;
import com.fastchat.sdk.utils.MessageUtils;
import com.fastchat.sdk.manager.MmvkManger;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.common.manager.ChatFileManager;
import com.zhengshuo.phoenix.common.manager.GroupInfoManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsManager;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsResultAction;
import com.zhengshuo.phoenix.ui.chat.ChatContract;
import com.zhengshuo.phoenix.ui.chat.weight.ChatInputView;
import com.zhengshuo.phoenix.ui.chat.weight.emojicon.Emojicon;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.CommonUtils;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;
import com.zhengshuo.phoenix.widget.CustomTitleBar;

import java.util.List;
import java.util.UUID;


/**
 * @Description: 聊天页
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class ChatFragment extends BaseFragment implements ChatContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener {
    private ChatContract.Presenter presenter;
    private ChatAdapter recyclerViewAdapter;
    private ChatInputView chatInputView;
    private FastVoiceRecorderView voiceRecorderView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private CustomTitleBar title_bar;
    private RelativeLayout rightClickLayout;
    private int chatType;
    private String toChatUsername;
    private static int[] itemNames = {R.string.attach_picture, R.string.attach_take_pic,R.string.attach_voice, R.string.attach_video,R.string.attach_file, R.string.attach_location,R.string.attach_card,R.string.attach_read_hot};
    private static int[] itemIcons = {R.mipmap.xiangce, R.mipmap.paizhao, R.mipmap.tonghua, R.mipmap.shiping, R.mipmap.wenjian, R.mipmap.weizhi, R.mipmap.mingpian, R.mipmap.yeuhoujifen};
    //RecyclerView 的滚动状态
    private int RVScrollState = 0;//0是停止滚动，1是开始滚动，2正在滑动
    private ClipboardManager clipboard;
    private PopupWindowHelper menuHelper;
    private ReferenceMessage mReferenceMessage;//引用消息


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }



    @Override
    protected void initLocalData() {
        ChatFileManager.get().clearImageOrVideoMessage();
        menuHelper = new PopupWindowHelper();
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        sendName = UserManager.get().getMyNick();
        initData();
        initLiveData();
    }

    private void initData() {
        Bundle fragmentArgs = getArguments();
        if (fragmentArgs == null || presenter == null) {
            getActivity().finish();
            return;
        }

        chatType = fragmentArgs.getInt("chatType", MessageUtils.CHAT_SINGLE);
        toChatUsername = fragmentArgs.getString("conversationId");

        presenter.initData(fragmentArgs);
    }


    private void initLiveData() {
        LiveDataBus.get().with(EventConstants.CONVERSATION_READ).postValue(toChatUsername);//发送通知告诉会话列表页该会话变成已读

        LiveDataBus.get().with(EventConstants.ACTION_MESSAGE_WITHDRAW, HTMessage.class).observe(getViewLifecycleOwner(), new Observer<HTMessage>() {
            @Override
            public void onChanged(HTMessage message) {
                if (message==null) {
                    return;
                }
                presenter.onMessageWithdraw(message);
            }
        });

        LiveDataBus.get().with(EventConstants.ACTION_MESSAGE_FORWARD, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                HTMessage message = intent.getParcelableExtra("message");
//                presenter.onMeesageForward(message);
            }
        });

        LiveDataBus.get().with(EventConstants.ACTION_NEW_MESSAGE, HTMessage.class).observe(getViewLifecycleOwner(), new Observer<HTMessage>() {
            @Override
            public void onChanged(HTMessage message) {
                if (message==null) {
                    return;
                }
                presenter.onNewMessage(message);
            }
        });


        LiveDataBus.get().with(EventConstants.APP_ON_BACK, Boolean.class).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isOnBack) {
                if (isOnBack) {
                    rightClickLayout.setTag("start");
                    title_bar.setRightText("自动发");
                    handler.removeCallbacks(MyRunnable);
                }
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_MESSAGE_EMPTY, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String id = intent.getStringExtra("id");
//                if (toChatUsername.equals(id)) {
//                    presenter.onMessageClear();
//                }
            }
        });

        LiveDataBus.get().with(EventConstants.CMD_DELETE_FRIEND, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra(HTConstant.JSON_KEY_USERID);
//                if (getActivity() != null) {
//                    if (userId.equals(toChatUsername)) {
//                        CommonUtils.showToastShort(getActivity(), getString(R.string.just_delete_friend));
//
//                        getActivity().finish();
//                    }
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_REMOVED_FROM_GROUP, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                if (getActivity() != null) {
//                    if (userId.equals(toChatUsername)) {
//                        CommonUtils.showToastShort(getActivity(), getString(R.string.just_delete_group));
//                        getActivity().finish();
//                    }
//                }
            }
        });




        LiveDataBus.get().with(EventConstants.ACTION_DELETE_GROUP, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String groupId = intent.getStringExtra("userId");
//                if (getActivity() != null) {
//                    if (groupId.equals(toChatUsername)) {
//                        getActivity().finish();
//                    }
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_HAS_CANCELED_NO_TALK, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                String content = intent.getStringExtra("content");
//
//                if (toChatUsername.equals(userId) && !GroupInfoManager.getInstance().isGroupSilent(toChatUsername)) {
//                    chatInputView.getEditText().setHint("");
//                    chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
//                }
//                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_HAS_NO_TALK, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                String content = intent.getStringExtra("content");
//
//                if (toChatUsername.equals(userId)) {
//                    Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
//                    if (!GroupInfoManager.getInstance().isManager(toChatUsername)) {
//                        chatInputView.getEditText().setText("");
//                        chatInputView.getEditText().setHint("已被管理员禁言");
//                        chatInputView.getEditText().clearFocus();
//                        chatInputView.getEditText().setInputType(InputType.TYPE_NULL);
//                    }
//
//
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_UPDATE_CHAT_TITLE, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra(HTConstant.JSON_KEY_USERID);
//                String userNick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
//                if (userId.equals(toChatUsername) && getActivity() != null) {
//                    ((ChatActivity) getActivity()).setTitle(userNick);
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_SET_OR_CANCEL_GROUP_MANAGER, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String groupId = intent.getStringExtra("groupId");
//                String userId = intent.getStringExtra("userId");
//                if (toChatUsername.equals(groupId) && UserManager.get().getMyUserId().equals(userId)) {
//                    int action = intent.getIntExtra("action", 0);
//                    if (action == 30002) {
//                        Toast.makeText(getActivity(), "群主设置你为管理员", Toast.LENGTH_SHORT).show();
//                        checkSilent();
//
//
//                    } else if (action == 30003) {
//                        Toast.makeText(getActivity(), "群主取消了你的管理员身份", Toast.LENGTH_SHORT).show();
//                        checkSilent();
//                    }
//
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.RED_PACKET_HAS_GOT, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String msgId = intent.getStringExtra("msgId");
//                String whiosRP = intent.getStringExtra("whiosRP");
//                presenter.sendRedCmdMessage(whiosRP, msgId);
            }
        });


        //群有新公告
        LiveDataBus.get().with(EventConstants.NEW_GROUP_NOTICE, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String groupId = intent.getStringExtra("groupId");
//
//                if (!toChatUsername.equals(groupId)) {
//                    return;
//                }
//                String content = intent.getStringExtra("content");
//                String title = intent.getStringExtra("title");
//                String id = intent.getStringExtra("id");
//                String preId = MmvkManger.getIntance().getAsString("group_notice" + BaseApplication.getInstance().getUsername() + groupId);
//                if (!TextUtils.isEmpty(id) && !id.equals(preId)) {
//                    showNewNoticeDialog(title, content, id);
//                }
            }
        });


        LiveDataBus.get().with(EventConstants.XMPP_LOGIN_OR_RE_LOGIN, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
                 if (chatType == 2) {
                    presenter.getGroupInfoInServer(toChatUsername);
                }
            }
        });

        //被单个禁言
        LiveDataBus.get().with(EventConstants.NO_TALK_USER, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                String content = intent.getStringExtra("content");
//                if (toChatUsername.equals(userId)) {
//                    CommonUtils.showToastShort(context, content);
//                    if (!GroupInfoManager.getInstance().isManager(toChatUsername)) {
//                        chatInputView.getEditText().setText("");
//                        chatInputView.getEditText().setHint("群已被禁言");
//                        chatInputView.getEditText().clearFocus();
//                        chatInputView.getEditText().setInputType(InputType.TYPE_NULL);
//
//                    }
//                }
            }
        });

        //被解除单个禁言
        LiveDataBus.get().with(EventConstants.NO_TALK_USER_CANCEL, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                String content = intent.getStringExtra("content");
//                if (toChatUsername.equals(userId)) {
//                    CommonUtils.showToastShort(context, content);
//
//
//                    if (!GroupInfoManager.getInstance().isGroupSilent(toChatUsername)) {
//                        chatInputView.getEditText().setHint("");
//                        chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
//                    }
//
//
//                }
            }
        });

    }


    @Override
    protected void initView(View mRootView) {
        title_bar = requireActivity().findViewById(R.id.title_bar);
        rightClickLayout  = title_bar.getRightLayout();
        rightClickLayout.setTag("start");

        voiceRecorderView = mRootView.findViewById(R.id.voice_recorder);
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);
        setRecyclerViewAndAdapter(mRootView);
        chatInputView = mRootView.findViewById(R.id.inputView);
        if (chatType == MessageUtils.CHAT_SINGLE) {
            chatInputView.initView(getActivity(), refreshLayout, itemNames, itemIcons);
        } else {
            chatInputView.initView(getActivity(), refreshLayout, itemNames, itemIcons);
            checkSilent();
        }
    }

    private void setRecyclerViewAndAdapter(View mRootView) {
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void checkSilent() {
        if (GroupInfoManager.getInstance().isGroupSilent(toChatUsername) && !GroupInfoManager.getInstance().isManager(toChatUsername)) {
            chatInputView.getEditText().setText("");
            chatInputView.getEditText().setHint("群已被禁言");
            chatInputView.getEditText().clearFocus();
            chatInputView.getEditText().setInputType(InputType.TYPE_NULL);

        } else {
            chatInputView.getEditText().setHint("");
            chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    protected void initEvent() {
        setListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        if (chatType == MessageUtils.CHAT_GROUP) {
            chatInputView.getEditText().addTextChangedListener(new TextWatcher() {
                boolean isDelAt = false;
                int strLength = 0;
                int indexAt = 0;
                String nick;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s != null && s.toString().endsWith(" ") && s.toString().contains("@")) {
                        //字符串不为空，并且以空格结尾，此时判断空格和@之间是否是一个@用户
                        indexAt = s.toString().lastIndexOf("@")+1;
                        nick= s.toString().substring(indexAt, s.length()-1);
                        if (presenter.isHasAtNick(nick)) {
                            isDelAt = true;
                            strLength = s.length();
                            return;
                        }
                    }
                    isDelAt = false;
                    strLength = 0;
                    indexAt = 0;

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        presenter.startChooseAtUser();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isDelAt && s.length()!= 0 && s.length() < strLength) {
                        chatInputView.getEditText().setText(s.subSequence(0, indexAt-1));
                        chatInputView.getEditText().setSelection(chatInputView.getEditText().getText().length());
                        presenter.deleteAtUser(nick);
                    }
                }
            });


        }

        refreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (chatInputView.isShown()) {
                    chatInputView.hideEmotionLayout(false);
                    chatInputView.hideSoftInput();
                }
                return false;
            }
        });

        //手机输入法键盘 Enter键直接发消息
        chatInputView.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String trim = chatInputView.getEditText().getText().toString().trim();
                    chatInputView.getEditText().getText().clear();
                    if (!TextUtils.isEmpty(trim)) {
                        presenter.sendTextMessage(trim,mReferenceMessage);
                    }
                    return true;
                }
                return false;
            }
        });
        chatInputView.setInputViewLisenter(new MyInputViewListener());
        //解决swipelayout与Recyclerview的冲突
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RVScrollState = newState;
            }

        });



        rightClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onRightClick","进来");
                String tag = (String) rightClickLayout.getTag();
                if ("start".equals(tag)) {
                    rightClickLayout.setTag("end");
                    title_bar.setRightText("停止");
                    handler.post(MyRunnable);
                } else {
                    rightClickLayout.setTag("start");
                    title_bar.setRightText("自动发");
                    handler.removeCallbacks(MyRunnable);
                }
            }
        });

        rightClickLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showTimeDialog();
                return true;
            }
        });
    }


    /**
     * 计时和计数
     */
    private void showTimeDialog() {
        InputStringDialog mInputStringDialog = new InputStringDialog(mActivity, String.valueOf(send_rate), String.valueOf(send_count),new InputStringDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String temp_send_rate,String temp_send_count) {
                send_rate = Integer.parseInt(temp_send_rate);
                send_count = Integer.parseInt(temp_send_count);
                handler.removeCallbacks(MyRunnable);
                handler.postDelayed(MyRunnable, send_rate);
            }
        });
        mInputStringDialog.show();
    }


    private String sendName;
    private int send_rate = 500;//1秒钟一次
    private int send_count = 2000;//2000条停止
    private int countContent = 0;//初始值从1开始

    private  Runnable MyRunnable = new Runnable(){

        @Override
        public void run() {
            countContent = countContent+1;
            Message message = handler.obtainMessage();
            message.what = 1000;
            message.obj = countContent;
            message.sendToTarget();
            handler.postDelayed(this,send_rate);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(MyRunnable);
        FastChatRowVoicePlayer voicePlayer = FastChatRowVoicePlayer.getInstance(mContext);
        if(voicePlayer.isPlaying()) {
            voicePlayer.stop();
            voicePlayer.release();
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    int content = (Integer) msg.obj;
                    presenter.sendTextMessage(content+"_"+sendName,null);
                    if (content ==send_count) {
                        handler.removeCallbacks(MyRunnable);
                    }
                    break;
            }
        }
    };





    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        presenter.loadMoreMessages();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        HTMessage mHTMessage = (HTMessage) adapter.getItem(position);
        if (mHTMessage==null) {
            return;
        }
        switch (view.getId()){
            case R.id.msg_status://消息失败
                showReSendDialog(mHTMessage);
                break;
            case R.id.bubble:
                if (mHTMessage.getType() == HTMessage.Type.IMAGE) {//图片消息
                    HTMessageImageBody htMessageImageBody = (HTMessageImageBody) mHTMessage.getBody();
                    String remotePath = htMessageImageBody.getRemotePath();
                    jumpToPhotoShowActivitySingle(remotePath);
                }else if(mHTMessage.getType() == HTMessage.Type.VIDEO){
                    HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) mHTMessage.getBody();
                    String remotePath = htMessageVideoBody.getRemotePath();
                    String fileName = htMessageVideoBody.getFileName();
                    jumpToPlayViewActivity(remotePath,fileName);
                }else if(mHTMessage.getType() == HTMessage.Type.VOICE){
                    boolean speakerOn = SettingsManager.getInstance().getSettingMsgSpeaker();
                    if (!speakerOn) {
                        ToastUtil.showToastView(mContext);
                    }
                    ImageView iv_voice = (ImageView) adapter.getViewByPosition(mRecyclerView, position,R.id.iv_voice);
                    ImageView iv_unread_voice = (ImageView) adapter.getViewByPosition(mRecyclerView, position,R.id.iv_unread_voice);
                    ProgressBar progress_bar = (ProgressBar) adapter.getViewByPosition(mRecyclerView, position,R.id.progress_bar);
                    new VoicePlayClickListener(mHTMessage,  iv_voice, iv_unread_voice, progress_bar,recyclerViewAdapter, getActivity()).onClick(view);
                }
                break;
            case R.id.ll_reference:
                if (mHTMessage.getType() == HTMessage.Type.TEXT) {//文本消息
                    HTMessageTextBody htMessageBody = (HTMessageTextBody) mHTMessage.getBody();
                    ReferenceMessage reference = htMessageBody.getReference();
                    if (reference != null) {
                        if (!StringUtil.isBlank(reference.getMessageId())) {
                            int reference_message_type = reference.getMsgType();
                            if (reference_message_type == 2001) {//文本消息

                            } else if (reference_message_type == 2002) {//图片引用
                                JSONObject body_jsonObject_big = htMessageBody.getBodyJson();
                                JSONObject reference_jsonObject = body_jsonObject_big.getJSONObject("reference");
                                JSONObject body_jsonObject_little = reference_jsonObject.getJSONObject("body");
                                String remotePath = body_jsonObject_little.getString("remotePath");
                                jumpToPhotoShowActivitySingle(remotePath);
                            }else if (reference_message_type == 2004) {//视频引用
                                JSONObject body_jsonObject_big = htMessageBody.getBodyJson();
                                JSONObject reference_jsonObject = body_jsonObject_big.getJSONObject("reference");
                                JSONObject body_jsonObject_little = reference_jsonObject.getJSONObject("body");
                                String remotePath = body_jsonObject_little.getString("remotePath");
                                String fileName = body_jsonObject_little.getString("fileName");
                                jumpToPlayViewActivity(remotePath,fileName);
                            }
                        }
                    }
                }
                break;
        }
    }



    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        HTMessage mHTMessage = (HTMessage) adapter.getItem(position);
        if (mHTMessage==null) {
            return false;
        }

        if (mHTMessage.getType() == HTMessage.Type.TEXT) {
            int action = mHTMessage.getIntAttribute("action", 0);
            if ((action < 2006 && action > 1999) || action == 6001) { //群相关通知消息||撤回消息
                return false;
            }
        }

        showDefaultMenu(view,mHTMessage,position);
        return true;
    }


    /**
     * 弹出消息长按菜单
     * @param v
     * @param message
     */
    private void showDefaultMenu(View v, HTMessage message, int position) {
        menuHelper.initMenu(mContext);
        menuHelper.setDefaultMenus();
        menuHelper.setOutsideTouchable(true);
        setMenuVisibleByMsgType(message);
        menuHelper.setOnPopupMenuItemClickListener(new FastPopupWindow.OnPopupWindowItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItemBean item) {
                int itemId = item.getItemId();
                if(itemId == R.id.action_id_copy) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((HTMessageTextBody) message.getBody()).getContent()));
                }else if(itemId == R.id.action_id_recall) {
                    presenter.withdrawMessage(message, position);
                }else if(itemId == R.id.action_id_forward) {
                    ToastUtil.ss("转发");
                    forwardMessage(message);
                }else if(itemId == R.id.action_id_yinyong) {
                    presenter.referenceMessage(message);
                }else if(itemId == R.id.action_id_delete) {
                    presenter.deleteSingChatMessage(message);
                }else if(itemId == R.id.action_id_play) {
                    boolean speakerOn = SettingsManager.getInstance().getSettingMsgSpeaker();
                    if (speakerOn) {
                        item.setTitle(getString(R.string.action_play_receiver));
                        item.setResourceId(R.mipmap.tingtong);
                    }else{
                        item.setTitle(getString(R.string.action_play_speaker));
                        item.setResourceId(R.mipmap.yangshenqi);
                    }
                    presenter.switchVoicePlay(!speakerOn);
                }
                return true;
            }
        });
        menuHelper.show(getView(), v);
    }

    private void setMenuVisibleByMsgType(HTMessage message) {
        HTMessage.Type type = message.getType();
        menuHelper.findItemVisible(R.id.action_id_play, false);
        menuHelper.findItemVisible(R.id.action_id_copy, true);
        menuHelper.findItemVisible(R.id.action_id_forward, true);
        menuHelper.findItemVisible(R.id.action_id_yinyong, true);
        menuHelper.findItemVisible(R.id.action_id_delete, true);

        // 消息发送3分钟后不能撤回
        long duration = System.currentTimeMillis() - message.getTime();
        if (duration > 180000) {
            menuHelper.findItemVisible(R.id.action_id_recall, false);
        } else {
            menuHelper.findItemVisible(R.id.action_id_recall, true);
        }



        switch (type) {
            case TEXT:

                break;
            case VOICE:
                menuHelper.findItemVisible(R.id.action_id_play, true);
                MenuItemBean voiceMenuItem = menuHelper.findItem(R.id.action_id_play);
                boolean speakerOn = SettingsManager.getInstance().getSettingMsgSpeaker();
                if (speakerOn) {
                    voiceMenuItem.setTitle(getString(R.string.action_play_receiver));
                    voiceMenuItem.setResourceId(R.mipmap.tingtong);
                }else{
                    voiceMenuItem.setTitle(getString(R.string.action_play_speaker));
                    voiceMenuItem.setResourceId(R.mipmap.yangshenqi);
                }
                menuHelper.findItemVisible(R.id.action_id_forward, false);
                menuHelper.findItemVisible(R.id.action_id_copy, false);
                break;
            case IMAGE:
            case VIDEO:
            case LOCATION:
                menuHelper.findItemVisible(R.id.action_id_copy, false);
                break;
        }

        if(message.getDirect() == HTMessage.Direct.RECEIVE){
            menuHelper.findItemVisible(R.id.action_id_recall, false);
        }
    }




    private class MyInputViewListener implements ChatInputView.InputViewListener {

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_RECORD_AUDIO)){
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new FastVoiceRecorderView.EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        presenter.sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), Constants.PERMS_RECORD_AUDIO, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
            }
            return false;
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {

        }

        @Override
        public void onSendButtonClicked(String content) {
            presenter.sendTextMessage(content,mReferenceMessage);
        }

        @Override
        public boolean onEditTextLongClick() {
            return false;
        }

        @Override
        public void onEditTextUp() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToBottom();
                }
            }, 100);
        }


        @Override
        public void onAlbumItemClicked() {//相册
            if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_WRITE_READ)){
                presenter.selectPicFromLocal(getActivity());
            }else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), Constants.PERMS_WRITE_READ, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        presenter.selectPicFromLocal(getActivity());
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
            }

        }

        @Override
        public void onPhotoItemClicked() {//拍照
            if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_CAMERA_RECORD_AUDIO_WRITE)){
                presenter.selectPicFromCamera(getActivity());
            }else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), Constants.PERMS_CAMERA_RECORD_AUDIO_WRITE, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        presenter.selectPicFromCamera(getActivity());
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
            }
        }

        @Override
        public void onLocationItemClicked() {
            if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_LOCATION_CODE)){
                FastGaodeMapActivity.actionStartForResult(mActivity, 100);
            }else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), Constants.PERMS_LOCATION_CODE, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        FastGaodeMapActivity.actionStartForResult(mActivity, 100);
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
            }

        }

        @Override
        public void onVideoItemClicked() {

        }

        @Override
        public void onCallItemClicked() {

        }

        @Override
        public void onFileItemClicked() {


        }


        @Override
        public void onContentCardClicked() {
            if (chatType == 2) {
                CommonUtils.showInputDialog(getContext(), "您确定要震群内所有人吗？", "请输入内容，或者点击确定直接震", "震所有群友上线", new CommonUtils.DialogClickListener() {
                    @Override
                    public void onCancleClock() {

                    }

                    @Override
                    public void onPriformClock(String msg) {
                        sendCMDandHTmsg(msg);
                    }
                });
            } else {

                presenter.startCardSend(getActivity());
            }
        }

        @Override
        public void onAfterReadHot() {

        }

        @Override
        public void onMoreButtonClick() {
        }

        @Override
        public void onCancelYinYongClick() {

        }

    }


    private void sendCMDandHTmsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = "震所有群友上线";
        }

        JSONObject body = new JSONObject();
        body.put("action", 40001);

        JSONObject data = new JSONObject();
        data.put("groupId", toChatUsername);
        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
        if (htGroup == null) {
            return;
        }
        data.put("groupName", htGroup.getGroupName());
        data.put("nick", UserManager.get().getMyNick());
        data.put("avatar", UserManager.get().getMyAvatar());
        data.put("content", msg);
        body.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(BaseApplication.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(toChatUsername);
        customMessage.setBody(body.toJSONString());
        customMessage.setChatType(ChatType.groupChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });


        presenter.sendTextMessage(msg,null);

    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }


    @Override
    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }


    private void scrollToBottom() {

        if (RVScrollState == 0 && recyclerViewAdapter.getItemCount() > 0) {
            mRecyclerView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);
        }


    }

    @Override
    public void insertRecyclerView(int position, int count, int type) {
        recyclerViewAdapter.notifyItemRangeInserted(position, count);
        if (type == 1) {
            //收到新消息
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int visibleposition = layoutManager.findLastCompletelyVisibleItemPosition();
            if ((recyclerViewAdapter.getItemCount() - visibleposition) < 5) {
                scrollToBottom();
            }
        } else if (type == 2) {
            //发出一条新消息
            scrollToBottom();
        }


    }


    /**
     * 引用消息
     * @param htMessage
     */
    @Override
    public void showReferenceView(HTMessage htMessage) {
        if (htMessage==null) {
            return;
        }

        LinearLayout ll_reference = chatInputView.getYinYongLayout_parent();
        TextView tv_reference = chatInputView.getYinYongLayout_TextView();

        if (ll_reference.getVisibility()==View.GONE) {
            ll_reference.setVisibility(View.VISIBLE);
        }

        //先从缓存取，缓存没有再从消息体中取
        String userId = htMessage.getUsername();
        if (htMessage.getChatType() == ChatType.groupChat) {
            userId = htMessage.getFrom();
        }
        String nick = UserManager.get().getUserNick(userId);
        if (StringUtil.isBlank(nick)) {
            //此处只需要判断nick是否为空，因为avatar即使本地存了，也是为空的
            nick = htMessage.getStringAttribute("nick");
        }

        String content = "";
        HTMessageBody htMessageBody = htMessage.getBody();
        if (htMessage.getType() == HTMessage.Type.TEXT) {
            content = ((HTMessageTextBody) htMessageBody).getContent();
            if (!TextUtils.isEmpty(content)) {
                content = SmileUtils.getSmiledText(mContext, content).toString();
                LinkifySpannableUtils.getInstance().setSpan(mContext, tv_reference, 99999);
            }

        }else if(htMessage.getType() == HTMessage.Type.IMAGE){
            content = "[图片]";
        }else if(htMessage.getType() == HTMessage.Type.VIDEO){
            content = "[视频]";
        }
        tv_reference.setText(String.format("%s：%s", nick,content));



        String referenceId  = htMessage.getMsgId();
        mReferenceMessage = new ReferenceMessage();
        mReferenceMessage.setMessageId(referenceId);

        int msgType = 2001;
        HTMessage.Type type = htMessage.getType();
        if (type == HTMessage.Type.TEXT) {

            msgType = 2001;
        } else if (type == HTMessage.Type.IMAGE) {
            msgType = 2002;
        } else if (type == HTMessage.Type.VOICE) {
            msgType = 2003;
        } else if (type == HTMessage.Type.VIDEO) {
            msgType = 2004;
        } else if (type == HTMessage.Type.FILE) {
            msgType = 2005;
        } else if (type == HTMessage.Type.LOCATION) {
            msgType = 2006;
        }
        mReferenceMessage.setMsgType(msgType);
        mReferenceMessage.setName(nick);
        mReferenceMessage.setBody(htMessage.getBody());
    }

    /**
     * 隐藏引用View
     */
    @Override
    public void hideReferenceView() {
        mReferenceMessage = null;
        LinearLayout ll_reference = chatInputView.getYinYongLayout_parent();
        if (ll_reference.getVisibility()==View.VISIBLE) {
            ll_reference.setVisibility(View.GONE);
        }
    }


    @Override
    public void loadMoreMessageRefresh(int position, int count) {
        recyclerViewAdapter.notifyItemRangeInserted(position, count);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (count > 0) {
            layoutManager.scrollToPositionWithOffset(count - 1, 80);
        }


    }

    @Override
    public void initRecyclerView(List<HTMessage> messageList) {
        recyclerViewAdapter = new ChatAdapter(messageList);
        recyclerViewAdapter.setOnItemChildClickListener(this);
        recyclerViewAdapter.setOnItemChildLongClickListener(this);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        scrollToBottom();
//        recyclerViewAdapter.setOnResendViewClick(new ChatAdapter.OnResendViewClick() {
//
//            @Override
//            public void resendMessage(HTMessage htMessage) {
//                showReSendDialog(htMessage);
//            }
//
//            @Override
//            public void onRedMessageClicked(HTMessage htMessage, String evnId) {
//
//            }
//
//            @Override
//            public void onTransferMessageClicked(final JSONObject jsonObject, String transferId) {
//
//            }
//
//            @Override
//            public void onAvatarLongClick(String userId) {
//                if (chatType == MessageUtils.CHAT_GROUP && !HTApp.getInstance().getUsername().equals(toChatUsername)) {
//
//                    if (presenter.isHasAt(userId)) {
//                        return;
//                    }
//                    String realNick = UserManager.get().getUserRealNick(userId);
//                    setAtUserStytle(realNick, false);
//                    presenter.setAtUser(realNick, userId);
//
//                }
//            }
//
//
//            @Override
//            public void onItemClick(HTMessage htMessage, int position) {
//
//            }
//
//            @Override
//            public void onAvatarClick(String userId) {
//                if (userId.equals(UserManager.get().getMyUserId())) {
//                    return;
//                }
//                if (chatType == 2) {
//                    if (GroupInfoManager.getInstance().isManager(toChatUsername)) {
//
//                        new HTAlertDialog(getActivity(), null, new String[]{"查看资料", "禁言"}).init(new HTAlertDialog.OnItemClickListner() {
//                            @Override
//                            public void onClick(int position) {
//                                switch (position) {
//                                    case 0:
//                                        startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId", userId));
//
//                                        break;
//                                    case 1:
//                                        GroupInfoManager.getInstance().addSilentUsers(toChatUsername, userId);
//                                        break;
//                                }
//
//
//                            }
//                        });
//                    }
//                    return;
//                }
//                startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId", userId));
//
//            }
//
//            @Override
//            public void onImageMessageClick(HTMessage htMessage) {
////                List<HTMessage> htMessageList=new ArrayList<>();
////                htMessageList.add(htMessage);
//                int indexPage = ChatFileManager.get().getImageOrVideoMessage().indexOf(htMessage);
//                Intent intent = new Intent(getActivity(), ShowBigImageActivity.class);
//                intent.putExtra("indexPage", indexPage);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onVideoMessageClick(HTMessage htMessage) {
//                int indexPage = ChatFileManager.get().getImageOrVideoMessage().indexOf(htMessage);
//                Intent intent = new Intent(getActivity(), ShowBigImageActivity.class);
//                intent.putExtra("indexPage", indexPage);
//
//                startActivity(intent);
//
//
//            }
//        });

    }

    public void setAtUserStyle(String atUserNick, boolean isFromChooseList) {
        //isFromChooseList是输入@号进行选择的
        String originContent = chatInputView.getEditText().getText().toString();
        String content = "@" + atUserNick + " ";
        if (isFromChooseList) {
            content = atUserNick + " ";
        }


        chatInputView.getEditText().setText(originContent + content);
        chatInputView.getEditText().setSelection((originContent + content).length());
    }

    @Override
    public void updateRecyclerView(int position) {
        recyclerViewAdapter.notifyItemChanged(position);
    }


    @Override
    public void deleteItemRecyclerView(int position) {
        recyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyClear() {
        recyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onGroupInfoLoaded() {
    }



    private void forwardMessage(HTMessage htMessage) {

    }


    /**
     * 重新发送消息
     *
     * @param htMessage
     */
    private void showReSendDialog(final HTMessage htMessage) {
        presenter.resendMessage(htMessage);
    }

    public void onBackPressed() {
        if (!chatInputView.interceptBackPress()) {
            getActivity().finish();
        }
    }



    @Override
    public void showNewNoticeDialog(String title, String content, String id) {
        CommonUtils.showAlertDialog(getActivity(), title, content, new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {


            }

            @Override
            public void onCancleClock() {

            }
        });
        MmvkManger.getInstance().putString("group_notice" + BaseApplication.getInstance().getUsername() + toChatUsername, id);
    }

}
