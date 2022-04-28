package com.zhengshuo.phoenix.ui.mine;

import android.view.View;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.base.UploadLogUtilFragment;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.dialog.TwoButtonDialog;
import com.zhengshuo.phoenix.viewmodel.LoginViewModel;
import com.fastchat.sdk.logcollect.logcat.LogcatHelper;

import butterknife.OnClick;

/**
 * @Description: 我的
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0010
 */
public class MeFragment extends UploadLogUtilFragment {
    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void InitViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getLoginOut().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> stringResource) {
                if (stringResource.status == Status.SUCCESS) {
                    BaseApplication.getInstance().logoutApp(0);
                } else if (stringResource.status == Status.LOADING) {

                } else if (stringResource.status == Status.ERROR) {

                }
            }
        });
    }

    @OnClick({R.id.logout,R.id.upload,R.id.delete})
    public void onClick(View mView) {
        switch (mView.getId()){
            case R.id.logout:
                showExitDialog();
                break;
            case R.id.upload:
                showUploadDialog();
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
        }

    }


    /**
     * 退出登录dialog
     */
    private void showExitDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(mActivity, "确定退出登录吗", "取消", "确定",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        loginViewModel.loginOut();
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();
    }


    /**
     * 上传日志dialog
     */
    private void showUploadDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(mActivity, "确定上传日志吗", "取消", "确定",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        uploadLog();
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();
    }



    /**
     * 删除日志dialog
     */
    private void showDeleteDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(mActivity, "确定删除日志吗", "取消", "确定",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        LogcatHelper.getInstance().logDelete(true);//删除本地log日志
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();
    }


}
