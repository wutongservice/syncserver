package com.borqs.sync.server.rpc.service.datasync.syncML;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 4/13/12
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SyncListener {
    public void onBeginSync(SyncContext context);
    public void onEndSync(SyncContext context);
}
