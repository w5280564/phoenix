package com.fastchat.sdk.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fastchat.sdk.db.model.GroupModel;

import java.util.List;

@Dao
public interface GroupDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGroupList(List<GroupModel> htGroupList);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGroup(GroupModel htGroup);


    @Query("SELECT * FROM `ht_group`")
    List<GroupModel>  getAllGroups();


    @Query("DELETE FROM `ht_group` WHERE groupId =:groupId")
    void deleteGroup(String groupId);
}
