package com.fastchat.sdk.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.fastchat.sdk.db.model.ConversationModel;
import java.util.List;

@Dao
public interface ConversationDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveConversation(ConversationModel conversation);


    @Query("SELECT * FROM `conversation`")
    List<ConversationModel> getConversationList();

    @Query("DELETE FROM `conversation` WHERE userId =:chatTo")
    void deleteConversation(String chatTo);
}
