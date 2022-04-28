package com.zhengshuo.phoenix.ui;

import android.view.KeyEvent;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;

/**
 * @Description: 首页
 * @Author: ouyang
 * @CreateDate: 2022/3/10 0009
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {

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