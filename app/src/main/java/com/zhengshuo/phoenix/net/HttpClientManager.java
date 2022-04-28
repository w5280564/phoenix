package com.zhengshuo.phoenix.net;

import android.content.Context;

/**
 * @Description: HttpClient管理器
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class HttpClientManager {
    private static final String TAG = "HttpClientManager";
    private static HttpClientManager instance;
    private Context context;
    private RetrofitClient client;

    private HttpClientManager(Context context) {
        this.context = context;
        client = new RetrofitClient(context, YuRuanTalkUrl.DOMAIN);
    }

    public static HttpClientManager getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpClientManager.class) {
                if (instance == null) {
                    instance = new HttpClientManager(context);
                }
            }
        }

        return instance;
    }

    public RetrofitClient getClient() {
        return client;
    }

}
