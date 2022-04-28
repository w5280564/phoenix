package com.zhengshuo.phoenix.ui.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.ChatType;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.manager.HTChatManager;
import com.fastchat.sdk.model.CmdMessage;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.HTConstant;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.SearchFriendNetViewModel;
import com.zhengshuo.phoenix.widget.CustomTitleBar;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 添加好友验证页
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0010
 */
public class AddFriendDetailActivity extends BaseActivity {
    @BindView(R.id.person_img)
    ImageView person_img;
    @BindView(R.id.nick_nameTxt)
    TextView nick_nameTxt;
    @BindView(R.id.iv_sex)
    ImageView iv_sex;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.ll_apply)
    LinearLayout ll_apply;
    @BindView(R.id.apply_Edit)
    EditText apply_Edit;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.send_mess_Txt)
    TextView send_mess_Txt;
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    private String friendUserId,friendImId;
    private SearchFriendNetViewModel viewModel;
    private String remark = "";


    public static void actionStart(Context context, SearchFriendInfoBean mSearchFriendInfoBean) {
        Intent intent = new Intent(context, AddFriendDetailActivity.class);
        intent.putExtra("mSearchFriendInfoBean", mSearchFriendInfoBean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_friend_detail;
    }


    @Override
    protected void initLocalData(Intent mIntent) {
        SearchFriendInfoBean mSearchFriendInfoBean = (SearchFriendInfoBean) mIntent.getSerializableExtra("mSearchFriendInfoBean");
        if (mSearchFriendInfoBean == null) {
            return;
        }

        handleData(mSearchFriendInfoBean);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchFriendNetViewModel.class);
        viewModel
                .getAddFriend()
                .observe(
                        this,
                        new Observer<Resource<String>>() {
                            @Override
                            public void onChanged(
                                    Resource<String> addFriendResultResource) {
                                if (addFriendResultResource.status == Status.SUCCESS) {
                                    ToastUtil.ss("好友申请已提交");
//                                    sendCmdNotice();
                                } else if (addFriendResultResource.status == Status.ERROR) {
                                    ToastUtil.ss(addFriendResultResource.message);

                                }
                            }
                        });
    }

    @Override
    protected void initEvent() {
        apply_Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = s.toString().trim();
                tv_number.setText(ss.length() + "/40");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void handleData(SearchFriendInfoBean obj) {
        friendUserId = obj.getUserId();
        friendImId = obj.getImId();
        titleBar.setTitle(obj.getNickname());
        ImgLoader.getInstance().displayCrop(mActivity, person_img, obj.getHeadImg(), R.mipmap.error_image_placeholder);
        nick_nameTxt.setText(obj.getNickname());
        tv_id.setText("ID：" + friendUserId);
        if (UserManager.get().getMyUserId().equals(obj.getUserId()) || obj.getFriendStatus() == 0) {
            ll_apply.setVisibility(View.GONE);
            send_mess_Txt.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.send_mess_Txt)
    public void onClick() {
        remark = StringUtil.getEditText(apply_Edit);
        viewModel.inviteFriend(friendUserId,remark);
    }



    private void sendCmdNotice() {
        JSONObject data = new JSONObject();
        data.put("ADD_REASON", remark);
        data.put("imId", UserManager.get().getMyImId());
        data.put("userId", UserManager.get().getMyUserId());
        data.put("nick", UserManager.get().getMyNick());
        data.put("avatar", UserManager.get().getMyAvatar());
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("action", HTConstant.APPLY_FRIEND);
        bodyJson.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(bodyJson.toJSONString());
        customMessage.setFrom(UserManager.get().getMyImId());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(friendImId);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.sendToTarget();
            }

            @Override
            public void onFailure() {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = R.string.apply_fail_2;
                message.sendToTarget();
            }
        });
    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    int resId = msg.arg1;
                    ToastUtil.ss(resId);
                    finish();
                    break;
                case 1002:
                    //发送透传消息成功
                    ToastUtil.ss(R.string.apply_success);
                    finish();
                    break;
            }
        }
    };

}
