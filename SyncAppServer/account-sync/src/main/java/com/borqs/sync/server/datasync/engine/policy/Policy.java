/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine.policy;

/**
 * Date: 1/9/12
 * Time: 3:02 PM
 * Borqs project
 */
public abstract class Policy {
    /**
     * if conflict happens, force to update or not
     * @return
     */
    public abstract boolean forceUpdate();
}
