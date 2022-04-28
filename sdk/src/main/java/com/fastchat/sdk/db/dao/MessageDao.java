package com.fastchat.sdk.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fastchat.sdk.db.model.MessageModel;
import java.util.List;

@Dao
public interface MessageDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMessage(MessageModel message);



    @Query("DELETE FROM `message` WHERE msgId =:msgId")
    void deleteHTMessage(String msgId);


    @Query("DELETE FROM `message` WHERE msgFrom =:chatTo OR msgTo =:chatTo ")
    void deleteUserHTMessage(String chatTo);



    @Query("SELECT * from `message` where msgTo=:chatTo OR (msgFrom==:chatTo and chatType =:chatType) ORDER BY msgTime DESC LIMIT 20")
    List<MessageModel> getAllMessages(String chatTo, int chatType);



    @Query("SELECT * from `message` where (msgTo =:chatTo OR (msgFrom==:chatTo and chatType =:chatType)) and msgTime <:timestamp ORDER BY msgTime DESC LIMIT :pageSize ")
    List<MessageModel> loadMoreMsgFromDB(String chatTo,long timestamp, int chatType, int pageSize);



    @Query("SELECT * from `message` where body  like '%' || :content || '%' ORDER BY msgTime")
    List<MessageModel> searchMsgFromDB(String content);



    @Query("SELECT * from `message` where msgId=:msgId LIMIT 1")
    MessageModel getMessage(String msgId);


    @Query("SELECT * from `message` where msgTo=:chatTo OR (msgFrom=:chatTo and chatType =:chatType) ORDER BY msgTime DESC LIMIT 1")
    MessageModel getLastMessage(String chatTo, int chatType);


    @Query("SELECT * from `message` where msgTo=:chatTo OR (msgFrom=:chatTo and chatType =:chatType) ORDER BY msgTime LIMIT :offSize+1")
    MessageModel getLastMessageOffSize(String chatTo,int offSize, int chatType);

    @Query("DELETE FROM `message` WHERE msgFrom =:chatTo OR msgTo =:chatTo and msgTime<:timeStamp")
    void deleteHTMessageFromTimestamp(String chatTo,long timeStamp);

}
