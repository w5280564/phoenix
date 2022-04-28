package com.fastchat.sdk.utils.http;


import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author ouyang 服务器返回数据监听
 */
public interface OnHttpResListener {

	/**
	 *
	 * @param json_root 返回数据的根json
	 * @param code 服务器返回码
	 * @param msg  返回信息（提示语）
	 */
	 void onComplete(JSONObject json_root, int code, String msg);


}