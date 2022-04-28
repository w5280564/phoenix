package com.zhengshuo.phoenix.ui.chat.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.MmvkManger;
import com.fastchat.sdk.model.HTGroup;
import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.manager.GroupInfoManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.FastMainActivity;
import com.zhengshuo.phoenix.ui.chat.ChatPresenter;
import com.zhengshuo.phoenix.ui.chat.fragment.ChatFragment;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.GroupDetailViewModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @Description: 单聊
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0010
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    private String toChatUsername;
    public int chatType;
    private GroupDetailViewModel mGroupDetailViewModel;
    private ChatPresenter chatPresenter;


    public static void actionStart(Context context,String conversationId,int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("chatType", chatType);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initLocalData(Intent mIntent) {
        toChatUsername = mIntent.getExtras().getString("conversationId");
        chatType = mIntent.getExtras().getInt("chatType", MessageUtils.CHAT_SINGLE);
        setActivityTitle();
    }

    private void setActivityTitle() {
        if (chatType == MessageUtils.CHAT_SINGLE) {
            title = UserManager.get().getUserNick(toChatUsername);
        } else if (chatType == MessageUtils.CHAT_GROUP) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
            GroupInfoManager.getInstance().setAtTag(toChatUsername,false);
            if (htGroup != null) {
                title = htGroup.getGroupName();
            }
        }
    }

    @Override
    protected void initView() {
        activityInstance = this;
        chatFragment= new ChatFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_chat, chatFragment);
        transaction.commit();
        chatFragment.setArguments(getIntent().getExtras());
        chatPresenter = new ChatPresenter(chatFragment);
    }

    public String getToChatUsername() {
        return toChatUsername;
    }




    @Override
    protected void onStop() {
        super.onStop();
        activityInstance = null;
    }



    @Override
    public void onRightClick() {
        if (chatType == MessageUtils.CHAT_SINGLE) {

        } else if (chatType == MessageUtils.CHAT_GROUP) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra("conversationId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivity();
        chatFragment.onBackPressed();
    }

    @Override
    public void onLeftClick() {
        super.onLeftClick();
        onBackPressed();
    }

    private void toMainActivity() {
        if (isSingleActivity(this)) {
            Intent intent = new Intent(this, FastMainActivity.class);
            startActivity(intent);
        }
    }


    public boolean isSingleActivity(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo) list.get(0)).numRunning == 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityInstance = this;
//        NotifierManager.getInstance().cancel(Integer.parseInt(toChatUsername));todo
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (chatPresenter != null) {
            chatPresenter.onResult(requestCode, resultCode, data,this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initViewModel() {
        mGroupDetailViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        if (chatType==MessageUtils.CHAT_GROUP) {
            mGroupDetailViewModel.getGroupUserListViewModel().observe(this, new Observer<Resource<JSONArray>>() {
                @Override
                public void onChanged(Resource<JSONArray> listResource) {
                    if (listResource.status == Status.SUCCESS) {
                        JSONArray jsonArray = listResource.data;
                        if (jsonArray != null) {
                            MmvkManger.getInstance().putJSONArray(toChatUsername + "_allMembers", jsonArray);
                            Set<String> memberUserIds = new HashSet<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject userInfo = jsonArray.getJSONObject(i);
                                String imId = userInfo.getString("imId");
                                String nick = userInfo.getString("nickname");
                                String avatar = userInfo.getString("headImg");
                                //保存用户信息
                                UserManager.get().saveUserNickAvatar(imId, nick, avatar);
                                memberUserIds.add(userInfo.getString("userId"));
                            }
                            MmvkManger.getInstance().putStringSet(toChatUsername + "_allMembers_userId", memberUserIds);
                        }
                    }else if (listResource.status == Status.ERROR) {
                        ToastUtil.ss(listResource.message);
                    }
                }
            });
            mGroupDetailViewModel.getGroupUserList(toChatUsername);
        }

    }
}
