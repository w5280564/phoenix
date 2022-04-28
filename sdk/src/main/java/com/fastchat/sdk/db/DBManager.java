package com.fastchat.sdk.db;

import android.content.Context;
import android.os.Environment;

import androidx.room.Room;

import com.fastchat.sdk.db.dao.ConversationDao;
import com.fastchat.sdk.db.dao.GroupDao;
import com.fastchat.sdk.db.dao.MessageDao;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.utils.Logger;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

/**
 * @Description: 数据库管理类
 * @Author: ouyang
 * @CreateDate: 2022/4/1
 */
public class DBManager {
    private final String DB_NAME_FORMAT = "user_%s";
    private static final String DB_NAME = ".db";
    private static volatile DBManager instance;
    private Context context;
    private YuRuanTalkDatabase database;
    private String currentUserId;

    private DBManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager(context);
                }
            }
        }

        return instance;
    }

    /**
     * 打开指定用户数据库
     *
     * @param userId
     */
    public void openDb(String userId) {
        if (currentUserId != null) {
            if (!currentUserId.equals(userId)) {
                closeDb();
            } else {
                Logger.d("DB", "user:" + userId + ", has opened db.");
                return;
            }
        } else {
            // do nothing
        }
        currentUserId = userId;
        String userIdMD5 = userId;



        WCDBOpenHelperFactory factory = new WCDBOpenHelperFactory()
                .writeAheadLoggingEnabled(true)       // 打开WAL以及读写并发，可以省略让Room决定是否要打开
                .asyncCheckpointEnabled(true);        // 打开异步Checkpoint优化，不需要可以省略

        database = Room.databaseBuilder(context, YuRuanTalkDatabase.class, getFileName()) //dbName可以使用单独的名字或者绝对路径
                //.allowMainThreadQueries()   // 允许主线程执行DB操作，一般不推荐
                .openHelperFactory(factory)   // 重要：使用WCDB打开Room
                .fallbackToDestructiveMigration()
                .build();
        Logger.d("DB", "openDb,userId:" + currentUserId);
    }


    private String getFileName() {
        String userName = HTPreferenceManager.getInstance().getUser().getUsername();
        return  String.format(DB_NAME_FORMAT, userName)+DB_NAME;
    }

    private boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void closeDb() {
        if (database != null) {
            Logger.d("DB", "closeDb,userId:" + currentUserId);
            database.close();
        }
        currentUserId = "";
    }


    public ConversationDao getConversationDao() {
        if (database == null) {
            Logger.e("DB", "Get Dao need openDb first.");
            return null;
        }
        return database.getConversationDao();
    }

    public GroupDao getGroupDao() {
        if (database == null) {
            Logger.e("DB", "Get Dao need openDb first.");
            return null;
        }
        return database.getGroupDao();
    }

    public MessageDao getMessageDao() {
        if (database == null) {
            Logger.e("DB", "Get Dao need openDb first.");
            return null;
        }
        return database.getMessageDao();
    }
}
