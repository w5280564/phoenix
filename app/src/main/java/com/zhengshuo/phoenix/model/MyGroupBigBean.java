package com.zhengshuo.phoenix.model;

import com.baozi.treerecyclerview.annotation.TreeDataType;
import com.google.gson.annotations.SerializedName;
import com.zhengshuo.phoenix.ui.group.adapter.GroupTreeItemOne;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@TreeDataType(iClass = GroupTreeItemOne.class)
public class MyGroupBigBean {


    @SerializedName("count")
    private Integer count;
    @SerializedName("groupList")
    private List<GroupListBean> groupList;
    @SerializedName("listName")
    private String listName;


}
