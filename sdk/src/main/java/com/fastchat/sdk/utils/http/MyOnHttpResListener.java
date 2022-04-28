package com.fastchat.sdk.utils.http;


import com.alibaba.fastjson.JSONObject;

/**
 * ouyang
 */
public abstract class MyOnHttpResListener implements OnHttpResListener {

	@Override
	public abstract void onComplete(JSONObject json_root, int code, String msg);


}
