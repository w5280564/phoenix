package com.zhengshuo.phoenix.model;

import java.util.List;

/**
 * @Description: GroupListTreeBean
 * @Author: ouyang
 * @CreateDate: 2022/3/18 0018
 */
public class GroupListTreeBean {
    private List<GroupListBean> joinGroupList;
    private List<GroupListBean> manageGroupList;
    private List<GroupListBean> ownGroupList;

    public List<GroupListBean> getJoinGroupList() {
        return joinGroupList;
    }

    public void setJoinGroupList(List<GroupListBean> joinGroupList) {
        this.joinGroupList = joinGroupList;
    }

    public List<GroupListBean> getManageGroupList() {
        return manageGroupList;
    }

    public void setManageGroupList(List<GroupListBean> manageGroupList) {
        this.manageGroupList = manageGroupList;
    }

    public List<GroupListBean> getOwnGroupList() {
        return ownGroupList;
    }

    public void setOwnGroupList(List<GroupListBean> ownGroupList) {
        this.ownGroupList = ownGroupList;
    }
}
