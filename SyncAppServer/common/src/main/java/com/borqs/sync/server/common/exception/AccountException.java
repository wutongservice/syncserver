package com.borqs.sync.server.common.exception;

import com.borqs.sync.server.common.util.Utility;

public class AccountException extends Exception {
    public int code;

    public AccountException(int code) {
        this.code = code;
    }

    public AccountException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
    }

    public AccountException(int code, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
    }

    public AccountException(int code, Throwable cause) {
        super(cause);        
        this.code = code;
    }

//    public static AccountException create(ResponseError error) {
//        int code = error.code;
//        String message = error.message==null?"":error.message.toString();
//
//        if(Utility.isEmpty(message)){
//            Throwable cause = error.getCause();
//            if(cause != null && !Utility.isEmpty(cause.getMessage())){
//                message = cause.getMessage();
//            } else {
//                message = "Account server internal issue";
//            }
//        }
//
//        return new AccountException(code,  message);
//    }

    public static AccountException create(Throwable error) {
        int code = ErrorCode.UNKNOWN_ERROR;
        String message = error.getMessage();

        if(Utility.isEmpty(message)){
            Throwable cause = error.getCause();
            if(cause != null && !Utility.isEmpty(cause.getMessage())){
                message = cause.getMessage();
            } else {
                message = "Account server internal issue";
            }
        }

        return new AccountException(code, message);
    }

    public static AccountException create(String rawData) {
        int code = ErrorCode.UNKNOWN_ERROR;
        String message = "Runtime response:\n" + rawData==null?"null":rawData;
        return new AccountException(code, message);
    }
}
