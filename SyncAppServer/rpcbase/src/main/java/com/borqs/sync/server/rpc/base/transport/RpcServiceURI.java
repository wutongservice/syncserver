/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base.transport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Date: 9/16/11
 * Time: 4:31 PM
 * EXAMPLE: avro://192.168.1.1:8080/IDL?iface=com.borqs.sync.avro.test&token=111
 */
public class RpcServiceURI {
    public static final String URI_SCHEMA_SOCKET = "avro";
    public static final String URI_SCHEMA_HTTP = "http";

    private static final String URI_IDL_PATH = "/IDL";
    private static final String URI_PARAMETER_IFACE = "iface";
    private static final String URI_PARAMETER_TOKEN = "token";

    private URIData mData;

    public static boolean isValidSchema(String schema){
        return URI_SCHEMA_SOCKET.equals(schema) || URI_SCHEMA_HTTP.equalsIgnoreCase(schema);
    }

    public RpcServiceURI() {
        mData = new URIData();
        mData.schema = URI_SCHEMA_SOCKET;
        mData.typePath = URI_IDL_PATH;
    }

    public RpcServiceURI(String uri) throws URISyntaxException {
        this();
        parse(URI.create(uri));
    }

    public RpcServiceURI setIface(String iface) {
        mData.iface = iface;
        return this;
    }

    public RpcServiceURI setSchema(String schema){
        mData.schema = schema;
        return this;
    }

    public RpcServiceURI setHost(String host){
        mData.host = host;
        return this;
    }

    public RpcServiceURI setPort(int port){
        mData.port = port;
        return this;
    }

    public RpcServiceURI setToken(String token){
        mData.token = token;
        return this;
    }

    public String getSchema(){
        return mData.schema;
    }

    public int getPort(){
        return mData.port;
    }

    public String getHost(){
        return mData.host;
    }

    public String getToken(){
        return mData.token;
    }

    public String getIface(){
        return mData.iface;
    }

    public URI getShortURI(){
        try {
            return new URI(mData.schema, null, mData.host, mData.port, null, null, null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String toString(){
        StringBuffer builder = new StringBuffer();
        builder.append(URI_PARAMETER_IFACE).append("=").append(mData.iface);
        if(mData.token != null){
            builder.append("&").append(URI_PARAMETER_TOKEN).append("=").append(mData.token);
        }
        try {
            return new URI(mData.schema, null, mData.host, mData.port, mData.typePath, builder.toString(), null)
                    .toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return  toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    private void parse(URI uri){
        mData.schema = uri.getScheme();
        mData.host = uri.getHost();
        mData.port = uri.getPort();
        mData.typePath = uri.getPath();
        Map<String,String> map = parseQuery(uri);
        mData.iface = map.get(URI_PARAMETER_IFACE);
        mData.token = map.get(URI_PARAMETER_TOKEN);
    }

    private static Map<String,String> parseQuery(URI uri){
        String query = uri.getQuery();
        if(query==null || query.isEmpty()){
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(query, "&");
        while(st.hasMoreTokens()){
            String token = st.nextToken();
            String[] str = token.split("=");
            if(str.length == 2){
                map.put(str[0], str[1]);
            }
        }
        return map;
    }

    private class URIData{
        private String schema;
        private String host;
        private int port;
        private String typePath;
        private String iface;
        private String token;
    }
}
