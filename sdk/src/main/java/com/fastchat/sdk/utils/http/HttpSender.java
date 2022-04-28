package com.fastchat.sdk.utils.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.fastchat.sdk.R;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.manager.MmvkManger;
import com.fastchat.sdk.utils.JsonUtil;
import com.fastchat.sdk.utils.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * Created by ouyang
 */

public class HttpSender {
	public static String TAG_API = "Api";
	private OnHttpResListener mListener;
	private String requestUrl = "",requestName="";
	private Map<String, Object> paramsMap;
	private Map<String, String > headerMap = new HashMap<>() ;//添加header头


	public HttpSender(String requestUrl, String requestName, Object mRequestObj,
                      OnHttpResListener mListener) {
		super();
		this.requestUrl = requestUrl;
		this.mListener = mListener;
		this.requestName = requestName;
		if (mRequestObj != null) {
			this.paramsMap = JsonUtil.Obj2Map(mRequestObj);
		}
		String token = MmvkManger.getInstance().getAsString("KEY_LOGIN_USER_TOKEN");
		if (!TextUtils.isEmpty(token)){
			headerMap.put("Authorization", token);
		}
	}



	public void sendPost() {
		requestPost();
	}



	public void sendGet() {
		requestGet();
	}





	/**
	 * get请求
	 */
	private void requestGet() {
		HashMap<String, String> upLoadMap = getRequestData();
		OkHttpUtils.get().url(requestUrl)
				.params(upLoadMap)
				.headers(headerMap)
				.build().execute(new StringDialogCallback());
	}



	/**
	 * POST请求
	 */
	private void requestPost() {
		setRequestData_POST();
		String request_data = JsonUtil.toJson(paramsMap);
		if (!TextUtils.isEmpty(request_data)) {
			OkHttpUtils.postString()
					.url(requestUrl)
					.content(request_data)
					.mediaType(MediaType.parse("application/json; charset=utf-8"))
					.headers(headerMap)
					.build().execute(new StringDialogCallback());
		}
	}




	/**
	 * 上传单个文件
	 */
	public void sendPostFile(File file) {
		requestPostFile(file);
	}


	/**
	 * POST 带文件上传时 调用此方法
	 *
	 * @param file
	 */
	private void requestPostFile(File file) {
		HashMap<String, String> upLoadMap = new HashMap<String, String>();

		if (paramsMap != null) {
			for (String key : paramsMap.keySet()) {
				Object requestParams = paramsMap.get(key);
				if(requestParams!=null){
					Logger.d(key + " = " + requestParams.toString());
					upLoadMap.put(key,requestParams.toString());
				}
			}
		}

		OkHttpUtils.post().url(requestUrl)
				.params(upLoadMap)
				.headers(headerMap)
				.addFile("file",file.getName(),file).build().execute(new StringDialogCallback());
	}


	/**
	 * 获取请求的数据
	 * @return
	 */
	private HashMap<String, String> getRequestData() {
		HashMap<String, String> upLoadMap = new HashMap<>();
		if (TextUtils.isEmpty(requestUrl)) {
			Logger.d(TAG_API,requestName + "请求 Url不对");
			return null;
		}
		if (paramsMap != null) {
			Logger.d(TAG_API,"请求名称：" + requestName);
			Logger.d(TAG_API,"请求Url：" + requestUrl);

			for (String key : paramsMap.keySet()) {
				Object requestParams = paramsMap.get(key);
				if(requestParams!=null){
					upLoadMap.put(key,requestParams.toString());
					Logger.d(TAG_API,key + " = " + requestParams.toString());
				}
			}
		}
		return upLoadMap;
	}


	/**
	 * 设置请求的数据(POST)
	 * @return
	 */
	private void setRequestData_POST() {
		if (TextUtils.isEmpty(requestUrl)) {
			Logger.d(TAG_API,requestName + "请求 Url不对");
			return ;
		}
		if (Logger.isDebug){
			if (paramsMap != null) {
				Logger.d(TAG_API,"请求名称：" + requestName);
				Logger.d(TAG_API,"请求Url：" + requestUrl);

				for (String key : paramsMap.keySet()) {
					Object requestParams = paramsMap.get(key);
					if(requestParams!=null){
						Logger.d(TAG_API,key + " = " + requestParams.toString());
					}

				}
			}
		}
	}




	public  class StringDialogCallback extends StringCallback {




		@Override
		public void onError(Call call, Exception e, int id) {
			String errorInfo =e.getMessage();
			Logger.d(TAG_API,requestName+"接口出现异常，异常信息：" +errorInfo);
			String errorMess = "服务维护中，请稍后再试..";
			int failure_code = SDKConstant.REQUEST_FAILURE_SERVER;
			if (!TextUtils.isEmpty(errorInfo)) {
				if (errorInfo.contains("org.apache.http.conn.ConnectTimeoutException")||errorInfo.contains("SocketTimeoutException")||errorInfo.contains("timeout")) {
					errorMess = "请求服务器超时";
					failure_code = SDKConstant.REQUEST_FAILURE_SERVER;
				}else if (errorInfo.contains("No address associated with hostname")||errorInfo.contains("Failed to connect to")) {
					errorMess = "无法连接到服务器,请检查网络连接";
					failure_code = SDKConstant.REQUEST_FAILURE_INTERNET;
				}else if (errorInfo.contains("request failed , reponse's code is : 404")) {
					errorMess = "服务维护中，请稍后再试..";
					failure_code = SDKConstant.REQUEST_FAILURE_SERVER;
				}
			}
			backError(errorMess,failure_code);
		}

		@Override
		public void onResponse(String json, int id) {
			Logger.d(TAG_API,requestName + "接口返回结果：" + json);
			JSONObject mJSONObject = JSONObject.parseObject(json);
			int code = mJSONObject.getInteger("code");//获取状态码
			String msg = mJSONObject.getString("msg");
			if (code == SDKConstant.REQUEST_SUCCESS_CODE) {
				if (mListener != null) {
					mListener.onComplete(mJSONObject, code, msg);
				}
			}
		}


		@Override
		public void onBefore(Request request, int id) {
			super.onBefore(request, id);
		}

		@Override
		public void onAfter(int id) {
			super.onAfter(id);
			//网络请求结束后关闭对话框
		}
	}


	private void backError(String failure_message,int failure_code){
		if (mListener != null) {
			mListener.onComplete(null, failure_code, failure_message);
		}
	}
}
