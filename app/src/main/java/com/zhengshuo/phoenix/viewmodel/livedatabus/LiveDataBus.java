package com.zhengshuo.phoenix.viewmodel.livedatabus;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

/**
 * 替代EventBus及RxBus
 * 代码源自：Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus
 * https://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651748475&idx=4&sn=8feb14dd49ce79726ecf12eb6c243740&chksm=bd12a1368a652820df7c556182d3494d84ae38d4aee4e84c48c227aa5083ebf2b1a0150cf1b5&mpshare=1&scene=1&srcid=1010fzmNILeVVxi5HsAG8R17#rd
 *
 * 基本使用：
 * 注册订阅：
 * LiveDataBus.get().getChannel("key_test", Boolean.class)
 *         .observe(this, new Observer<Boolean>() {
 *             @Override
 *             public void onChanged(@Nullable Boolean aBoolean) {
 *             }
 *         });
 * 发送消息：
 * LiveDataBus.get().getChannel("key_test").setValue(true);
 */
public final class LiveDataBus {

    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public <T> MutableLiveData<T> with(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    public MutableLiveData<Object> with(String key) {
        return with(key, Object.class);
    }

}