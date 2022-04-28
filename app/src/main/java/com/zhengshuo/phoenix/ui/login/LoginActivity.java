package com.zhengshuo.phoenix.ui.login;

import android.view.KeyEvent;
import androidx.fragment.app.FragmentTransaction;
import com.fastchat.sdk.client.HTClient;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;

/**
 * @Description: 登录页
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class LoginActivity extends BaseActivity {
    private LoginFragment loginFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        loginFragment= new LoginFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_login, loginFragment);
        transaction.commit();
        loginFragment.setArguments(getIntent().getExtras());
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
