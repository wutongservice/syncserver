package com.borqs.sync.server.rpc.service.datasync.syncML;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 4/13/12
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncManager {

    private Map<String,SyncContext> mSyncContextMap = Collections.synchronizedMap(new HashMap<String, SyncContext>());

    public void beginSyncOf(String userId, long since){
        if(mSyncContextMap.containsKey(userId)){
            onSyncError(mSyncContextMap.get(userId));
            mSyncContextMap.remove(userId);
        }
        mSyncContextMap.put(userId, new SyncContext(userId, since));
        onBeginSync(mSyncContextMap.get(userId));
    }

    public void endSyncOf(String userId){
        mSyncContextMap.remove(userId);
        onEndSync(mSyncContextMap.get(userId));
    }
    
    public SyncContext getSyncContext(String userId){
        return mSyncContextMap.get(userId);
    }

    private void onBeginSync(SyncContext context){

    }

    private void onEndSync(SyncContext context){

    }

    private void onSyncError(SyncContext context){

    }

    public void addSyncListener(SyncListener listener){

    }
}
