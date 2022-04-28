package com.fastchat.sdk.db;

import androidx.room.TypeConverter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TypeConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        JSONArray jsArr = JSONObject.parseArray(value);
        List<String> list = JSONObject.parseArray(jsArr.toJSONString(), String.class);
        return (ArrayList<String>) list;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(list));
        return   array.toJSONString();
    }
}
