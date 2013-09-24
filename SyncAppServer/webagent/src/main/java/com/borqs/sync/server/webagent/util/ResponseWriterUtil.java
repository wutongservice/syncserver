/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JsonWriter;

public class ResponseWriterUtil {

    public static void writeStringJson(String key, String value, ResponseWriter writer)
            throws IOException {
        JsonWriter jWriter = writer.asJsonWriter();
        jWriter.beginObject();
        jWriter.name(key).value(value);
        jWriter.endObject();
        jWriter.flush();
        jWriter.close();
    }

    public static void writeObjectJson(String value, ResponseWriter writer)
            throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(writer.asStream());
        bos.write(value.getBytes());
        bos.flush();
        bos.close();
    }
    
    public static void writeResult(ResponseWriter writer, int code, String content) throws IOException {
    	JsonWriter jsonWriter = writer.asJsonWriter();
    	jsonWriter.beginObject();
    	jsonWriter.name("code").value(code);
    	jsonWriter.name("content").value(content);
    	jsonWriter.endObject();
    	jsonWriter.flush();
    	jsonWriter.close();
    }
    
    public static void writeResultJsonp(ResponseWriter writer, int code, String content, String callback) throws IOException {
    	if(null == callback || "".equals(callback.trim())) {
    		writeResult(writer, code, content);
    		return;
    	}
    	
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", code);
		jsonObj.put("content", content);
		StringBuffer sb = new StringBuffer();
		sb.append(callback).append("(").append(jsonObj.toString())
				.append(");");
		writer.asStream().write(sb.toString().getBytes());
    }

    public static void writeMapJson(Map<String, String> names, ResponseWriter writer)
            throws IOException {
        JsonWriter jWriter = writer.asJsonWriter();
        jWriter.beginObject();
        Set<String> keys = names.keySet();
        for(String key : keys){
            jWriter.name(key).value(names.get(key));
        }
        jWriter.endObject();
        jWriter.flush();
        jWriter.close();
    }
}
