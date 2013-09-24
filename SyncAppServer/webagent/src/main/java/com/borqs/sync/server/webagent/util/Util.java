package com.borqs.sync.server.webagent.util;

import java.util.List;

public class Util {

	public static boolean stringEqual(String str1, String str2) {
		if (isEmpty(str1) && isEmpty(str2)) {
			return true;
		} else if (isEmpty(str1) && !isEmpty(str2)) {
			return false;
		} else if (!isEmpty(str1) && isEmpty(str2)) {
			return false;
		} else {
			return str1.equals(str2);
		}
	}

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

    public static boolean isEmptyList(List<?> list){
        return list == null || list.size() == 0;
    }
}
