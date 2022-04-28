package com.zhengshuo.phoenix.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.login.LoginActivity;
import com.fastchat.sdk.client.HTClient;
import com.gyf.barlibrary.ImmersionBar;

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
		if (HTClient.getInstance().isLoginEd()) {
			jumpToMainActivity();
		}else{
			jumpToLoginActivity();
		}
		finish();
	}


	/**
	 * 跳转到首页或欢迎页
	 */
	private void jumpToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


	/**
	 * 跳转到首页
	 */
	private void jumpToLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}


}
