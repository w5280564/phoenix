package com.fastchat.sdk.listener;

/**
 * Created by ouyang on 2017/2/13.
 * 
 */

public interface HTConnectionListener {
     public static int CONFLICT=1;
     public static int NUMORL=0;
     public static int RECONNECT_ERROR=2;
     public void onConnected(int type) ;
     public void onDisconnected();
     public void onConflict();


}
