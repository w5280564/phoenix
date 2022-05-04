package com.zhengshuo.phoenix.ui.start;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.ui.MainActivity;
import com.zhengshuo.phoenix.ui.login.LoginActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.zhengshuo.phoenix.util.StringUtil;

/**
 * 欢迎页
 */
public class SplashActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        goToWhere();
    }


    /**
     * 设置状态栏
     */
    private void setStatusBar() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)     //状态栏颜色，不写默认透明色
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    public void onBackPressed() {

    }


    /**
     * 根据情况跳转
     */
    private void goToWhere() {
        jumpToMainActivity();
        finish();
    }


    /**
     * 跳转到首页或欢迎页
     */
    private void jumpToMainActivity() {
        if (getFirstInit()) {
            saveFirstInit(false);
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    /**
     * 跳转到首页
     */
    private void jumpToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 修改首次打开记录
     * @param is
     */
    public void saveFirstInit(boolean is) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("launch", is);
        editor.commit();
    }

    /**
     * 是否首次登录
     * @return
     */
    public boolean getFirstInit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("launch", true);
    }


}
