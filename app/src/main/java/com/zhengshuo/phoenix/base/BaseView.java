package com.zhengshuo.phoenix.base;

import android.app.Activity;
import android.content.Context;

/**
 * Created by ouyang on 2017/6/21.
 * 
 */

public interface BaseView<T> {
    void setPresenter(T presenter);
    Context getBaseContext();
    Activity getBaseActivity();
}
