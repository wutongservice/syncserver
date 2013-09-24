/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.httpservlet;

import com.borqs.sync.server.common.json.JsonWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * User: b251
 * Date: 12/29/11
 * Time: 11:15 AM
 * Borqs project
 */
public final class ResponseWriter {
    private OutputStream mOutput;
    private ResponseWriter(){}

    public static ResponseWriter from(HttpServletResponse resp) throws IOException {
        ResponseWriter writer = new ResponseWriter();
        writer.mOutput = resp.getOutputStream();
        return writer;
    }

    public static ResponseWriter from(OutputStream os){
        ResponseWriter writer = new ResponseWriter();
        writer.mOutput = os;
        return writer;
    }

    public OutputStream asStream(){
        return mOutput;
    }

    public JsonWriter asJsonWriter(){
        return new JsonWriter(new OutputStreamWriter(mOutput));
    }

    public void release() {
        if(mOutput != null){
            try {
                mOutput.close();
            } catch (IOException e) {}
        }
    }
}
