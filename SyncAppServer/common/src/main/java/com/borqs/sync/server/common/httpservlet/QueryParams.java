/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.common.httpservlet;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.exception.ErrorCode;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class QueryParams extends HashMap<String, String>{
    private QueryParams() {
    }

    public static QueryParams create(HttpServletRequest req) {
        QueryParams qp = new QueryParams();
        qp.parseParams(req);
        return qp;
    }

    @SuppressWarnings("unchecked")
    private void parseParams(HttpServletRequest req) {
        try {
            Map m = req.getParameterMap();
            for (Object e0 : m.entrySet()) {
                Map.Entry<String, ?> e = (Map.Entry<String, ?>) e0;
                Object v = e.getValue();
                String vs = v instanceof String[]
                        ? StringUtils.join((String[]) v, "")
                        : ObjectUtils.toString(v, "");
                put(e.getKey(), vs);
            }
        } catch (Exception e) {
            throw new WebMethodException(e);
        }
    }

    public static class Value<T> {
        public final String key;
        public final T value;

        public Value(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public String getString(String k, String def) {
        Object v = get(k);
        return v != null ? ObjectUtils.toString(v) : def;
    }

    private static String checkValue(String k, String v) throws AccountException {
        if (v == null)
            throw new AccountException(ErrorCode.PARAM_ERROR, "Missing parameter '%s'", k);
        return v;
    }

    public String checkGetString(String k) throws AccountException {
        return checkValue(k, getString(k, null));
    }


    public long checkGetInt(String k) throws AccountException {
        String v = checkGetString(k);
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            throw new AccountException(ErrorCode.PARAM_ERROR, "Invalid parameter '%s'", k);
        }
    }

    public boolean checkGetBoolean(String k) throws AccountException {
        String v = checkGetString(k);
        return Boolean.parseBoolean(v);
    }
    
}
