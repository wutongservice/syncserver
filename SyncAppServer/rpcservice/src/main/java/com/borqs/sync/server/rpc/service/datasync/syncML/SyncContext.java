package com.borqs.sync.server.rpc.service.datasync.syncML;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 4/13/12
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncContext {
    private String mUserId;
    private long mSince;
    private boolean mClientUpdate;
    private boolean mClientChange;


    public SyncContext(String userId, long since){
        mUserId = userId;
        mSince = since;
    }

    public long getSince(){
        return mSince;
    }
    
    public String getUserId(){
        return mUserId;
    }

    public boolean hasClientUpdate(){
        return mClientUpdate;
    }

    public void setClientUpdate(boolean clientUpdate){
        mClientUpdate = clientUpdate;
    }

    public void setClientChange(boolean clientChange){
        mClientChange = clientChange;
    }

    public boolean hasClientChange(){
       return mClientChange;
    }
}
