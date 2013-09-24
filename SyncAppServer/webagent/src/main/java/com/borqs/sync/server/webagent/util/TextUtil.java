/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.util;

public class TextUtil {

	public static final boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}

	public static final boolean isDigitsOnly(String str){
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
	
	public static final boolean isVaildPhoneNumber(String number) {
	    boolean res = true;
	    if (isEmpty(number)){
            res = false;
        }
       
        if (!isDigitsOnly(number)){
            res = false;
        }       
        
        if (res){       
            // 11 digits number
            if (number.length() != 11){
                res = false;
            }
            
            if (!number.startsWith("1")){
                res = false;
            }
        }
        
        return res;
    }
	
	public static final boolean isVaildMailAddress(String address) {
	    int len = address.length();
        int firstAt = address.indexOf('@');
        int lastAt = address.lastIndexOf('@');
        int firstDot = address.indexOf('.', lastAt + 1);
        int lastDot = address.lastIndexOf('.');
        return firstAt > 0 && firstAt == lastAt && lastAt + 1 < firstDot
            && firstDot <= lastDot && lastDot < len - 1;
    }
}
