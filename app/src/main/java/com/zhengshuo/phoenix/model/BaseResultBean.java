package com.zhengshuo.phoenix.model;


import com.zhengshuo.phoenix.common.Constants;

/**
 * 网络请求结果基础类
 *
 * @param <T> 请求结果的实体类
 */
public class BaseResultBean<T> {
    public int code;
    public T data;
    public String msg;

    public BaseResultBean() {}

    public BaseResultBean(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public boolean isSuccess() {
        return code == Constants.REQUEST_SUCCESS_CODE;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
