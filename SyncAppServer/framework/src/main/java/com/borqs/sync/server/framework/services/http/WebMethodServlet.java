/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.framework.services.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.exception.SyncError;
import com.borqs.sync.server.common.exception.SyncServerRuntimeException;
import com.borqs.sync.server.framework.Charsets;
import com.borqs.sync.server.common.httpservlet.*;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONCreator;
import org.apache.commons.lang.StringUtils;

public class WebMethodServlet extends HttpServlet {
    private final Map<String, Invoker> mInvokers = new HashMap<String, Invoker>();

    private boolean mPrintErrorDetails = false;
    private String servletPath;

    public WebMethodServlet(HttpServletDelegate servletImpl) {
        initMethods(servletImpl);
    }
    
    public WebMethodServlet(HttpServletDelegate servletImpl, String servletPath) {
    	this.servletPath = servletPath;
        initMethods(servletImpl);
    }

    private void initMethods(HttpServletDelegate methodHolder) {
        Class clazz = methodHolder.getClass();
        System.out.println(clazz.toString());

        for (Method method : clazz.getMethods()) {
            if (!Modifier.isPublic(method.getModifiers()))
                continue;

            WebMethod ann = method.getAnnotation(WebMethod.class);
            if (ann == null)
                continue;

            String requestMethod = ann.value();
            if (mInvokers.containsKey(requestMethod))
                throw WebMethodException.from("Repetitive url '%s'", requestMethod);

            //check parameters' type
            for (Class pt : method.getParameterTypes()) {
                if (!pt.isAssignableFrom(HttpServletRequest.class)
                        && !pt.equals(HttpServletResponse.class)
                        && !pt.equals(QueryParams.class)
                        && !pt.equals(PostData.class)
                        && !pt.equals(ResponseWriter.class))
                    throw WebMethodException.from("Invalid parameter type '%s'", pt.getName());
            }

            //check return type
            Class rt = method.getReturnType();
            try {
                if(!rt.isAssignableFrom(Class.forName("void"))){
                    throw WebMethodException.from("Invalid return type '%s'", rt.getName());
                }
            } catch (ClassNotFoundException e) {}

            mInvokers.put(requestMethod, new Invoker(methodHolder, method));
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String reqMethod = req.getMethod();
        if (reqMethod.equals("GET") || reqMethod.equals("POST")) {
            super.service(req, resp);
        } else {
            resp.setStatus(405); // Method Not Allowed
        }
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            boolean b = before(req, resp);
            if (b) {
                process(req, resp);
            }
        } finally {
            after(req, resp);
        }
    }

    protected boolean before(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setCharacterEncoding(Charsets.DEFAULT);
        return true;
    }

    protected void after(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
    }

    protected void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = StringUtils.removeStart(
                joinIgnoreNull(req.getServletPath(), req.getPathInfo()), "/");
        
        if(null == servletPath) {
        	servletPath = "/";
        }

        String norooturl = ("/"+url).replaceFirst(servletPath.replaceAll("\\*", ""), "");
        
		Invoker invoker = mInvokers.get(norooturl);
        if (invoker != null) {
            invoker.invoke(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // Not Found
        }
    }

    private String joinIgnoreNull(String servletPath, String pathInfo) {
        return servletPath + (StringUtils.isEmpty(pathInfo)? "" : pathInfo);
    }

    private class Invoker {
        final Object mObj;

        final Method mMethod;

        private Invoker(Object obj, Method method) {
            this.mObj = obj;
            this.mMethod = method;
        }

        public void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            Class[] pts = mMethod.getParameterTypes();
            Object[] params = new Object[pts.length];
            try {
                for (int i = 0; i < pts.length; i++) {
                    Class pt = pts[i];
                    if (pt.isAssignableFrom(QueryParams.class)) {
                        QueryParams qp = QueryParams.create(req);
                        params[i] = qp;
                    } else if(pt.isAssignableFrom(PostData.class)){
                        PostData pds = PostData.from(req);
                        params[i] = pds;
                    } else if(pt.isAssignableFrom(ResponseWriter.class)){
                        ResponseWriter writer = ResponseWriter.from(resp);
                        params[i] = writer;
                    } else if (pt.isAssignableFrom(HttpServletRequest.class)) {
                        params[i] = req;
                    } else if (pt.isAssignableFrom(HttpServletResponse.class)) {
                        params[i] = resp;
                    }
                }

                Throwable err = null;
                Object r = null;
                writeHeader(resp, 200, "application/json");
                try {
                    r = mMethod.invoke(mObj, params);
                } catch (InvocationTargetException e) {
                    err = e.getTargetException();
                } catch (Throwable t) {
                    err = t;
                }

                if (err != null) {
                    writeError(err, resp, mPrintErrorDetails);
                }
            } finally {
                for(Object p : params){
                    if(p instanceof PostData){
                        ((PostData)p).release();
                    } else if( p instanceof ResponseWriter){
                        ((ResponseWriter)p).release();
                    }

                }
            }
        }
    }

    private static void writeBooleanResult(final Boolean result, HttpServletResponse resp) throws IOException {
        String json = JSONCreator.createJSON(new JSONCreator.Handler() {
            @Override
            public void generate(JSONCreator writer) throws JSONException {
                writer.put("result", String.valueOf(result));
            }
        }).toString();
        writeStringResult(resp, json);
    }

    private static void writeError(final Throwable err, HttpServletResponse resp,
            boolean printErrorDetails) throws IOException {
        if (err instanceof SyncServerRuntimeException) {
            String json = JSONCreator.createJSON(new JSONCreator.Handler() {
                @Override
                public void generate(JSONCreator writer) throws JSONException {
                    writer.put("error_code", 500);
                    writer.put("error_msg", err.getMessage());
                }
            }).toString();
            writeStringResult(resp, json);
        } else if(err instanceof SyncError) {
            String json = JSONCreator.createJSON(new JSONCreator.Handler() {
                @Override
                public void generate(JSONCreator writer) throws JSONException {
                    writer.put("error_code", ((SyncError) err).errno);
                    writer.put("error_msg", err.getMessage());
                }
            }).toString();
            writeStringResult(resp, json);
        } else if(err instanceof AccountException){
            String json = JSONCreator.createJSON(new JSONCreator.Handler() {
                @Override
                public void generate(JSONCreator writer) throws JSONException {
                    writer.put("error_code", ((AccountException) err).code);
                    writer.put("error_msg", err.getMessage());
                }
            }).toString();
            writeStringResult(resp, json);
        }
    }

    private static void writeHeader(HttpServletResponse resp, int statusCode, String contentType){
        resp.setCharacterEncoding(Charsets.DEFAULT);
        resp.setStatus(statusCode);
        resp.setContentType(contentType);
    }

    private static void writeStringResult(HttpServletResponse resp, String text) throws IOException {
        PrintWriter writer = getWriter(resp);
        resp.setHeader("Content-Encoding","gzip");
        writer.write(text);
        writer.flush();
        writer.close();
    }

    private static PrintWriter getWriter(HttpServletResponse response) throws IOException
    {
        ServletOutputStream sos = response.getOutputStream();
        GZIPOutputStream gzipos = new GZIPOutputStream(sos);
        return new PrintWriter(gzipos);
    }
}
