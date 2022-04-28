package com.zhengshuo.phoenix.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.model.Resource;
import com.zhengshuo.phoenix.model.Status;
import com.zhengshuo.phoenix.ui.MainActivity;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.zhengshuo.phoenix.widget.ClearWriteEditText;
import com.zhengshuo.phoenix.viewmodel.LoginViewModel;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 登录页
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class LoginFragment extends BaseFragment {
    @BindView(R.id.cet_login_phone)
    ClearWriteEditText cetLoginPhone;
    @BindView(R.id.cet_login_verify_code)
    ClearWriteEditText cetLoginVerifyCode;
    @BindView(R.id.cet_login_send_verify_code)
    Button cetLoginSendVerifyCode;
    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initLocalData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int type = bundle.getInt("type", 0);
            if (type == 1) {
                showConflictDialog();
            }
        }
    }

    /**
     * 账号异常退出弹窗
     */
    private void showConflictDialog() {
        String st = getResources().getString(R.string.Logoff_notification);

        // clear up global variables
        try {

            AlertDialog.Builder exceptionBuilder = new AlertDialog.Builder(getActivity());

            exceptionBuilder.setTitle(st);
            exceptionBuilder.setMessage(R.string.connect_conflict);
            exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            exceptionBuilder.setCancelable(false);
            exceptionBuilder.show();
        } catch (Exception e) {
        }

    }


    @Override
    protected void InitViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel
                .getLoginResult()
                .observe(
                        this,
                        new Observer<Resource<String>>() {
                            @Override
                            public void onChanged(Resource<String> resource) {
                                if (resource.status == Status.SUCCESS) {
                                    dismissLoadingDialog(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.ss("登录成功");
                                                    skipAnotherActivity(MainActivity.class);
                                                }
                                            });
                                } else if (resource.status == Status.LOADING) {
                                    showLoadingDialog("登录中...");
                                } else if (resource.status == Status.ERROR) {
                                    int code = resource.code;
                                    dismissLoadingDialog(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.ss(resource.message);
                                                }
                                            });
                                }
                            }
                        });

        // 获取验证码
        loginViewModel
                .getSendCodeState()
                .observe(
                        this,
                        new Observer<Resource<String>>() {
                            @Override
                            public void onChanged(Resource<String> resource) {
                                // 提示
                                if (resource.status == Status.SUCCESS) {
                                    ToastUtil.ss("验证码已发送");
                                } else if (resource.status == Status.LOADING) {

                                } else {
                                    ToastUtil.ss(resource.message);
                                    cetLoginSendVerifyCode.setEnabled(true);
                                }
                            }
                        });

        // 等待接受验证码倒计时， 并刷新及时按钮的刷新
        loginViewModel
                .getCodeCountDown()
                .observe(
                        this,
                        new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (integer > 0) {
                                    cetLoginSendVerifyCode.setText(integer + "s");
                                } else {
                                    // 当计时结束时， 恢复按钮的状态
                                    cetLoginSendVerifyCode.setEnabled(true);
                                    cetLoginSendVerifyCode.setText("发送验证码");
                                }
                            }
                        });
    }

    @OnClick({R.id.cet_login_send_verify_code, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cet_login_send_verify_code:
                String phoneNumber = cetLoginPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtil.ss("请输入手机号");
                    return;
                }
                sendCode(phoneNumber);
                break;
            case R.id.btn_login:
                String phoneStr = cetLoginPhone.getText().toString().trim();
                String codeStr = cetLoginVerifyCode.getText().toString().trim();

                if (TextUtils.isEmpty(phoneStr)) {
                    ToastUtil.ss("请输入手机号");
                    cetLoginPhone.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(codeStr)) {
                    ToastUtil.ss("请输入验证码");
                    cetLoginVerifyCode.setShakeAnimation();
                    return;
                }

                registerAndLogin(phoneStr, codeStr);
                break;
        }
    }


    /**
     * 请求发送验证码
     * @param phoneNumber 手机号
     */
    private void sendCode(String phoneNumber) {
        loginViewModel.sendCode(phoneNumber);
    }


    private void registerAndLogin(String phone, String code) {
        loginViewModel.registerAndLogin(phone, code);
    }

}
