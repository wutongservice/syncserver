/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.httpservlet;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ReflectibleService;

/**
 * Date: 12/28/11
 * Time: 6:14 PM
 * Refer to com.borqs.sync.server.services.http.MockServelet under framework test
 */
public abstract class HttpServletDelegate extends ReflectibleService {
    public HttpServletDelegate(Context context){
        super(context);
    }


//    @WebMethod("account_request1")
//    public Boolean accountRequest1(QueryParams qp) {
//        return true;
//    }
//
//    @WebMethod("account_request2")
//    public Boolean accountRequest2(QueryParams qp, PostData inputData) {
//        return true;
//    }
//
//    @WebMethod("account_request3")
//    public String accountRequest3(HttpServletRequest req, HttpServletResponse resp, QueryParams qp, PostData inputData) {
//        return "";
//    }
//    @WebMethod("account_request3")
//    public String accountRequest3(QueryParams qp, PostData inputData, ResponseWriter writer) {
//        return "";
//    }
}
