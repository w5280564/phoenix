package com.zhengshuo.phoenix.ui.conversation;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.common.FragmentKeyEventListener;
import com.zhengshuo.phoenix.ui.FastMainActivity;
import com.zhengshuo.phoenix.widget.menu.OnPopupMenuItemClickListener;
import com.zhengshuo.phoenix.widget.menu.PopupMenuHelper;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.MmvkManger;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.model.HTConversation;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.common.HTClientHelper;
import com.zhengshuo.phoenix.ui.chat.activity.ChatActivity;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;

/**
 * @Description: 消息列表页
 * @Author: ouyang
 * @CreateDate: 2022/3/22 0009
 */
public class ConversationFragment extends BaseFragment implements ConversationView, FragmentKeyEventListener {

    private RecyclerView mRecyclerView;
    private ConversationAdapter adapter;
    public RelativeLayout errorItem;
    public TextView errorText;
    public NewMessageListener mListener;
    private ConversationPresenter conPresenter;
    private int chatType = MessageUtils.CHAT_SINGLE;
    private PopupMenuHelper menuHelper;
    private FastMainActivity mMainActivity ;
    private float touchX;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation_list;
    }

    @Override
    public void setPresenter(ConversationPresenter presenter) {
        conPresenter = presenter;
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
    public void showItemDialog(View view,final HTConversation htConversation) {
        showDefaultMenu(view,htConversation);
    }

    private void showDefaultMenu(View view, HTConversation info) {
        menuHelper.addItemMenu(Menu.NONE, R.id.action_con_make_top, 1, getContext().getString(R.string.conversation_menu_make_top));
        menuHelper.addItemMenu(Menu.NONE, R.id.action_con_cancel_top, 2, getContext().getString(R.string.conversation_menu_cancel_top));
        menuHelper.addItemMenu(Menu.NONE, R.id.action_con_delete, 3, getContext().getString(R.string.conversation_menu_delete));

        menuHelper.initMenu(view);

        //检查置顶配置
        menuHelper.findItemVisible(R.id.action_con_make_top, info.getTopTimestamp() == 0);
        menuHelper.findItemVisible(R.id.action_con_cancel_top, info.getTopTimestamp() != 0);
        menuHelper.setOnPopupMenuItemClickListener(new OnPopupMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item, int menuPos) {
                int itemId = item.getItemId();
               if(itemId == R.id.action_con_make_top) {
                   conPresenter.setTopConversation(info);
                    return true;
                }else if(itemId == R.id.action_con_cancel_top) {
                   conPresenter.cancelTopConversation(info);
                    return true;
                }else if(itemId == R.id.action_con_delete) {
                   conPresenter.deleteConversation(info.getUserId());
                    return true;
                }
                return false;
            }
        });

        menuHelper.show((int) touchX, 0);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        conPresenter = new ConversationPresenter(this);
        if (context instanceof NewMessageListener) {
            mListener = ((NewMessageListener) context);
        }
        ///获取绑定的监听
        if (context instanceof FastMainActivity) {
            mMainActivity = (FastMainActivity)context;
            mMainActivity.setFragmentKeyEventListener(this); //设置监听
        }
    }

    private void onUnreadMsgChange() {
        mListener.onUnReadMessages(conPresenter.getUnreadMsgCount());
    }


    @Override
    protected void initLocalData() {
        initLiveData();
    }

    private void initLiveData() {
        LiveDataBus.get().with(EventConstants.CONVERSATION_READ, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
                if (toChatUsername == null) {
                    return;
                }
                HTConversation htConversation = HTClient.getInstance().conversationManager().getConversation(toChatUsername);
                if (htConversation == null) {
                    return;
                }

                conPresenter.markAllMessageRead(htConversation);
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_CONNECTION_CHANGED, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
                //   boolean isConnected = intent.getBooleanExtra("state", true);
//                if (isConnected) {
//                  //  errorItem.setVisibility(View.GONE);
//                } else {
////                    errorItem.setVisibility(View.VISIBLE);
////                    if (CommonUtils.isNetWorkConnected(getActivity())) {
////                        errorText.setText(R.string.can_not_connect_chat_server_connection);
////                    } else {
////                        errorText.setText(R.string.the_current_network);
////                    }
//                }

//                if (!CommonUtils.isNetWorkConnected(getActivity())) {
//                    errorItem.setVisibility(View.VISIBLE);
//                    errorText.setText(R.string.the_current_network);
//                } else {
//                    errorItem.setVisibility(View.GONE);
//                }
            }
        });

        LiveDataBus.get().with(EventConstants.ACTION_NEW_MESSAGE, HTMessage.class).observe(getViewLifecycleOwner(), new Observer<HTMessage>() {
            @Override
            public void onChanged(HTMessage htMessage) {
                if (htMessage==null) {
                    return;
                }
                conPresenter.onNewMsgReceived(htMessage);
            }
        });

        LiveDataBus.get().with(EventConstants.ACTION_MESSAGE_WITHDRAW, HTMessage.class).observe(getViewLifecycleOwner(), new Observer<HTMessage>() {
            @Override
            public void onChanged(HTMessage htMessage) {
                if (htMessage == null) {
                    return;
                }
                conPresenter.onNewMsgReceived(htMessage);
            }
        });

        LiveDataBus.get().with(EventConstants.CMD_DELETE_FRIEND, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
////                HTConversation conversation = HTClient.get().conversationManager().getConversation(userId);
////                if (conversation != null) {
//                conPresenter.deleteConversation(userId);
            }
        });


        LiveDataBus.get().with(EventConstants.DELETE_FRIEND_LOCAL, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
////                HTConversation conversation = HTClient.get().conversationManager().getConversation(userId);
////                if (conversation != null) {
//                conPresenter.deleteConversation(userId);
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_REMOVED_FROM_GROUP, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                conPresenter.deleteConversation(userId);
//                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
//                // 群退出时要删除会话
//                //   refreshALL();
            }
        });


        LiveDataBus.get().with(EventConstants.ACTION_DELETE_GROUP, String.class).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String toChatUsername) {
//                String userId = intent.getStringExtra("userId");
//                conPresenter.deleteConversation(userId);
//                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
//                // 群退出时要删除会话
//                //   refreshALL();
            }
        });
    }




    @Override
    public void onReceiveNewMessage(int position, HTConversation mHTConversation,int type) {//1更新 2插入
        if (adapter!=null) {
            if (type==1) {
                adapter.refreshNotifyItemChanged(position);
            }else{
                adapter.addData(position,mHTConversation);
            }
        }
        onUnreadMsgChange();
    }

    @Override
    public void adapterRefresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        onUnreadMsgChange();
    }


    @Override
    protected void initView(View mRootView) {
        errorItem = mRootView.findViewById(R.id.rl_error_item);
        errorText = errorItem.findViewById(R.id.tv_connect_errormsg);
        mRecyclerView = mRootView.findViewById(R.id.mRecyclerView);
        initRecyclerViewAndData();
        menuHelper = new PopupMenuHelper();
        conPresenter.start();
    }


    private void initRecyclerViewAndData() {
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        adapter = new ConversationAdapter(conPresenter.getAllConversations());
        mRecyclerView.setAdapter(adapter);
        adapter.setEmptyView(addEmptyView(R.string.message_text_noData_message, R.mipmap.zanwuxiaoxi));
    }


    @Override
    protected void initEvent() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HTConversation htConversation = (HTConversation) adapter.getItem(position);
                if (htConversation == null) {
                    return;
                }
                String userId = htConversation.getUserId();
                ChatType chatType = htConversation.getChatType();
                if (chatType == ChatType.singleChat) {
                    ConversationFragment.this.chatType = MessageUtils.CHAT_SINGLE;
                } else {
                    ConversationFragment.this.chatType = MessageUtils.CHAT_GROUP;
                }
                ChatActivity.actionStart(getActivity(),userId, ConversationFragment.this.chatType);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                HTConversation htConversation = (HTConversation) adapter.getItem(position);
                if (htConversation == null) {
                    return false;
                }
                showItemDialog(view,htConversation);
                return true;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        conPresenter.checkFriendsAndGroups();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = conPresenter.getUnreadMsgCount();
                if (count < 0) {
                    count = 0;
                }
//                ShortcutBadger.applyCount(getBaseContext(), count); //for 1.1.4+ todo
                MmvkManger.getInstance().putLong("BadgerCount", count);
                HTClientHelper.BadgerCount = count;
            }
        }).start();
    }

    @Override
    public void onFragmentKeyEvent(float touchX) {
        this.touchX = touchX;
    }


    public interface NewMessageListener {
        //返回多少条未读消息
        void onUnReadMessages(int count);
    }


}
