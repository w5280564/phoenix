package com.zhengshuo.phoenix.model;

import com.baozi.treerecyclerview.annotation.TreeDataType;
import com.google.gson.annotations.SerializedName;
import com.zhengshuo.phoenix.ui.group.adapter.GroupTreeItemTwo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: GroupListBean
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0018
 */
@NoArgsConstructor
@Data
@TreeDataType(iClass = GroupTreeItemTwo.class)
public class GroupListBean {
    @SerializedName("groupImId")
    private String groupImId;
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("showId")
    private String showId;
    @SerializedName("groupHeadImg")
    private String groupHeadImg;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("identity")
    private Integer identity;
}