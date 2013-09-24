/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.base;

public class AccountErrorCode {
    
    // Basic errors
    public static final int PARAM_ERROR = 9;
    public static final int GENERAL_ERROR = 1998;
    public static final int UNKNOWN_ERROR = 1999;

    protected static final int BASE_ERROR = 500;
    public static final int JSON_ERROR = BASE_ERROR + 1;
    public static final int DATA_ERROR = BASE_ERROR + 2;
    public static final int ENCODER_ERROR = BASE_ERROR + 3;
    public static final int RPC_ERROR = BASE_ERROR + 4;
    public static final int NET_ERROR = BASE_ERROR + 5;
    public static final int AUTH_ERROR = BASE_ERROR + 6;
    public static final int SFS_ERROR = BASE_ERROR + 7;
    public static final int IMAGE_ERROR = BASE_ERROR + 8;
    public static final int PROCESS_ERROR = BASE_ERROR + 9;
    public static final int NOTIFICATION_ERROR = BASE_ERROR + 10;
    public static final int NOT_FOUND_ERROR = BASE_ERROR + 11;
    public static final int DUP_USER_ERROR = BASE_ERROR + 12;
    public static final int NO_USER_EXIST_ERROR = BASE_ERROR + 13;

}
