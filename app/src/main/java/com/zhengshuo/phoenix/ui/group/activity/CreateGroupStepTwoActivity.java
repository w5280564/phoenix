package com.zhengshuo.phoenix.ui.group.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fastchat.sdk.utils.MessageUtils;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.model.GroupInfoBean;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.chat.activity.ChatActivity;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.FriendContactListPickViewModel;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建群聊第二步
 */
public class CreateGroupStepTwoActivity extends BaseActivity {
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    private FriendContactListPickViewModel viewModel;
    private ArrayList<String> userIds;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_group_step_two;
    }


    public static void actionStart(Context context, List<String> selectedMembers) {
        Intent intent = new Intent(context, CreateGroupStepTwoActivity.class);
        intent.putStringArrayListExtra("userIds", (ArrayList<String>) selectedMembers);
        context.startActivity(intent);
    }

    @Override
    protected void initLocalData(Intent mIntent) {
        userIds = mIntent.getStringArrayListExtra("userIds");
    }

    @Override
    protected void initView() {
        judgeButtonIsClickAble();
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(FriendContactListPickViewModel.class);
        viewModel.getCreateGroupViewModel().observe(this, new Observer<Resource<GroupInfoBean>>() {
            @Override
            public void onChanged(Resource<GroupInfoBean> objResource) {
                // 提示
                if (objResource.status == Status.SUCCESS) {
                    GroupInfoBean obj = objResource.data;
                    ToastUtil.ss(objResource.message);
                    ChatActivity.actionStart(mActivity,obj.getGroupImId(), MessageUtils.CHAT_GROUP);
                    LiveDataBus.get().with(EventConstants.CREATE_GROUP).postValue(true);
                    finish();
                } else if (objResource.status == Status.LOADING) {

                } else {
                    ToastUtil.ss(objResource.message);
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        addEditTextListener();
    }

    /**
     * 给editext添加监听
     */
    private void addEditTextListener() {
        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                judgeButtonIsClickAble();
            }
        });
    }


    /**
     * 判断按钮是否可以点击
     */
    private void judgeButtonIsClickAble() {
        if (StringUtil.isBlank(StringUtil.getEditText(et_name))) {
            btn_submit.setBackgroundResource(R.drawable.btn_shape_gray_round);
            btn_submit.setEnabled(false);
        } else {
            btn_submit.setBackgroundResource(R.drawable.btn_selector_round);
            btn_submit.setEnabled(true);
        }
    }


    @OnClick({R.id.btn_submit})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.btn_submit:
                String groupName = StringUtil.getEditText(et_name);

                viewModel.createGroup(groupName,"",userIds);
                break;
        }
    }




}
