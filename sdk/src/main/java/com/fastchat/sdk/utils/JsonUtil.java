package com.fastchat.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Description: Json转换工具类
 * @author ouyang
 * @toJson 将对象转换成json字符串
 * @getValue 在json字符串中，根据key值找到value
 * @json2Map 将json格式转换成map对象
 * @json2Bean 将json转换成bean对象
 * @json2List 将json格式转换成List对象
 * @Obj2Map obj 转为 map
 * 
 */
public class JsonUtil {





	/**将对象转换成字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		if (obj == null) {
			return "{}";
		}
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
		return jsonObject.toJSONString();
	}


	/**将数组转换成字符串
	 * @param mList
	 * @return
	 */
	public static <T> String arrayToJson(List<T> mList) {
		if (mList.isEmpty()) {
			return "{}";
		}
		JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(mList));
		return array.toJSONString();
	}


	/**
	 * 在json字符串中，根据key值找到value
	 *
	 * @param json
	 * @param key
	 * @return
	 */

	public static String getValue(String json, String key) {
		JSONObject jsonobject=JSONObject.parseObject(json);
		return jsonobject.get(key).toString();
	}






	/**
	 * 将json格式转换成map对象
	 *
	 * @param json
	 * @return
	 */
	public static Map<String, Object> json2Map(String json) {
		Map<String,Object> objMap =JSONObject.parseObject(json, Map.class);
		return objMap;
	}

	/**
	 * 将json格式字符串转换成bean对象
	 *
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Bean(String json, Class<T> clazz) {

		JSONObject jsonObject = JSONObject.parseObject(json);
		T obj = obj = JSONObject.toJavaObject(jsonObject, clazz);
		return obj;
	}


	/**
	 * 将json格式字符串转换成List对象
	 */
	public static <T> List<T> json2List(String json, Class<T> clazz) {
		List<T> mList = (List<T>)JSONObject.parseArray(json,clazz);
		return mList;
	}

	/**
	 * obj 转为 map
	 *
	 * @param obj
	 *            需要转的对象
	 * @return
	 */
	public static Map<String, Object> Obj2Map(Object obj) {
		if (obj != null) {
			return json2Map(toJson(obj));
		}
		return null;
	}



}
