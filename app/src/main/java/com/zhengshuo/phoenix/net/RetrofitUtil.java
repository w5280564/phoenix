package com.zhengshuo.phoenix.net;

import com.zhengshuo.phoenix.common.LogTag;
import com.fastchat.sdk.utils.JsonUtil;
import com.zhengshuo.phoenix.util.LogUtil;

import java.util.HashMap;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RetrofitUtil {
    private static final MediaType MEDIA_TYPE_JSON =
            MediaType.parse("application/json;charset=UTF-8");

    /**
     * 通过参数 Map 合集
     *
     * @param paramsMap
     * @return
     */
    public static RequestBody createJsonRequest(HashMap<String, Object> paramsMap) {
        String strEntity = JsonUtil.toJson(paramsMap);
        LogUtil.d(LogTag.API,"---请求data---" + strEntity);
        return RequestBody.create(MEDIA_TYPE_JSON, strEntity);
    }
}
