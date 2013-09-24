/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.providers;

import java.sql.ResultSet;

/**
 * Date: 3/13/12
 * Time: 2:26 PM
 * helper class to feedback the result during SQL query
 */
public interface CursorResultHandler {
    /**
     * called to parse the full ResultSet
     * @param item
     */
    public void onResult(ResultSet item);
}
