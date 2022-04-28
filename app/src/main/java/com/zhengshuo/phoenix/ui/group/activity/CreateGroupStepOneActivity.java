package com.zhengshuo.phoenix.ui.group.activity;

import android.content.Intent;

import androidx.lifecycle.Observer;

import com.zhengshuo.phoenix.common.EventConstants;
import com.zhengshuo.phoenix.ui.friend.activity.BaseFriendContactPickListActivity;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;

import java.util.List;

/**
 * 创建群主第一步
 */
public class CreateGroupStepOneActivity extends BaseFriendContactPickListActivity {


    @Override
    protected void initLocalData(Intent mIntent) {
        super.initLocalData(mIntent);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        LiveDataBus.get().with(EventConstants.CREATE_GROUP, Boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean event) {
                if (event == null) {
                    return;
                }
                if (event) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onRightClick() {
        List<String> selectedMembers = adapter.getSelectedMembers();
        if (selectedMembers.isEmpty() || selectedMembers.size() < 2) {
            ToastUtil.ss("群成员不能少于两人");
            return;
        }

        jumpToNextPage(selectedMembers);
    }

    private void jumpToNextPage(List<String> selectedMembers) {
        CreateGroupStepTwoActivity.actionStart(mActivity, selectedMembers);
    }

}

