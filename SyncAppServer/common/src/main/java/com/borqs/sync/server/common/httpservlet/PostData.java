/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.httpservlet;

import com.borqs.sync.server.common.json.JsonReader;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 5:12 PM
 * Borqs project
 */
public final class PostData {
    private InputStream mDataStream;
    private int mLength = 0;

    private PostData(){}

    public static PostData from(HttpServletRequest req) throws IOException {
        PostData postData = new PostData();
        postData.mLength = req.getContentLength();
        postData.mDataStream = req.getInputStream();
        return postData;
    }

    public int length() throws IOException {
        return mLength<=0?mDataStream.available():mLength;
    }

    public JsonReader asJsonReader(Charset charset) throws UnsupportedEncodingException {
        JsonReader reader = new JsonReader(new InputStreamReader(mDataStream, charset));
        reader.setLenient(true);
        return reader;
    }

    public InputStream asStream(){
        return mDataStream;
    }

    public String asString(Charset charset) throws IOException {
        StringWriter writer = new StringWriter(length());
        IOUtils.copy(mDataStream, writer, charset.name());
        return writer.toString();
    }

    public void release() {
        if(mDataStream != null){
            try {
                mDataStream.close();
            } catch (IOException e) {}
        }
    }
}
