package com.zhengshuo.phoenix.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class MyVideoBean {
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
}
