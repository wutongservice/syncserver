/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.util;

import com.borqs.sync.server.common.runtime.Context;

import java.util.logging.Logger;

public class WebLog {

    private static final String TAG = "webagent";

    public static Logger getLogger(Context context) {
        return context.getLogger(TAG);
    }

    public static Logger getLogger(Context context ,String tag) {
        return context.getLogger(tag);
    }

}
