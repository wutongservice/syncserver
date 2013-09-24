/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.service;

import com.borqs.sync.server.common.httpservlet.*;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.pim.group.GroupSyncHandler;

import java.io.IOException;

/**
 * User: b251
 * Date: 12/29/11
 * Time: 6:19 PM
 * Borqs project
 */
public class AccountSyncHttpDebugImpl extends HttpServletDelegate{

    public AccountSyncHttpDebugImpl(Context context) {
        super(context);
    }

    /**
     * For test by http://localhost:8880/contact/sync?id=226
     * @param qp
     * @param input
     * @param output
     * @throws IOException
     */
    @WebMethod("contact/sync")
    public void contact_sync(QueryParams qp, PostData input, ResponseWriter output) throws IOException {
        String targeId = qp.getString("id", "226");
        SocialContactSyncServiceImpl impl = new SocialContactSyncServiceImpl(mContext);
        impl.syncFromAccount(targeId, 0, System.currentTimeMillis());
    }

    /**
     * For test of group sync?id=226
     */
    @WebMethod("group/sync")
    public void group_sync(QueryParams qp, PostData input, ResponseWriter output) throws IOException {
        String targeId = qp.getString("id", "226");
        GroupSyncHandler gh = new GroupSyncHandler(mContext);
        gh.refreshContactGroup(targeId, System.currentTimeMillis());
    }
}
