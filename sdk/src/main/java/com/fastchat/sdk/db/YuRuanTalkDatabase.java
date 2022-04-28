package com.fastchat.sdk.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fastchat.sdk.db.dao.ConversationDao;
import com.fastchat.sdk.db.dao.GroupDao;
import com.fastchat.sdk.db.dao.MessageDao;
import com.fastchat.sdk.db.model.ConversationModel;
import com.fastchat.sdk.db.model.GroupModel;
import com.fastchat.sdk.db.model.MessageModel;

@Database(
        entities = {
                GroupModel.class,
                MessageModel.class,
                ConversationModel.class,
        },
        version = 2,
        exportSchema = true)

public abstract class YuRuanTalkDatabase extends RoomDatabase {

    public abstract ConversationDao getConversationDao();

    public abstract GroupDao getGroupDao();

    public abstract MessageDao getMessageDao();
}
