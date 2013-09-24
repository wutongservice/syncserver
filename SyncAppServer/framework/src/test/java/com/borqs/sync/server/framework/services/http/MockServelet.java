/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.http;

import com.borqs.sync.server.framework.Charsets;
import com.borqs.sync.server.common.httpservlet.*;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.json.JsonWriter;
import com.borqs.sync.server.common.runtime.Context;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

/**
 * User: b251
 * Date: 12/29/11
 * Time: 11:01 AM
 * Borqs project
 */
public class MockServelet extends HttpServletDelegate {
    public MockServelet(Context context) {
        super(context);
    }


    @WebMethod("test_query_params")
    public void do_request3(QueryParams params, final PostData input, final ResponseWriter output) throws IOException {
        Set<String> keys = params.keySet();
        JsonWriter writer = output.asJsonWriter();
        writer.beginObject();
        for(String s : keys){
            writer.name(s).value(params.get(s));
        }
        writer.endObject();
        writer.flush();
        writer.close();
    }

    @WebMethod("test_post_data_string")
    public void do_request4(QueryParams params, final PostData input, final ResponseWriter output) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output.asStream());
        writer.write(input.asString(Charsets.DEFAULT_CHARSET));
        writer.flush();
        writer.close();
    }

    @WebMethod("test_post_data_json")
    public void do_request5(QueryParams params, final PostData input, final ResponseWriter output) throws IOException, JSONException {
        JsonReader reader = input.asJsonReader(Charsets.DEFAULT_CHARSET);

        String returnName = "";
        String returnValue = "";
        reader.beginObject();
        while(reader.hasNext()){
            String nextName = reader.nextName();
            if("return_name".equals(nextName)){
                returnName = reader.nextString();
                continue;
            }

            if("return_value".equals(nextName)){
                returnValue = reader.nextString();
            }
        }
        reader.endObject();

        JsonWriter writer = output.asJsonWriter();
        writer.beginObject();
        writer.name(returnName).value(returnValue);
        writer.endObject();
        writer.flush();
        writer.close();
    }

    @WebMethod("test_response_writer")
    public void do_request6(QueryParams params, ResponseWriter output) throws IOException, JSONException {
        Set<String> keys = params.keySet();
        JsonWriter jWriter = output.asJsonWriter();
        jWriter.beginObject();
        for(String s : keys){
            jWriter.name(s).value(params.get(s));
        }
        jWriter.endObject();
        jWriter.flush();
        jWriter.close();
    }
}
