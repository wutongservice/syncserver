/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.http;

import com.borqs.sync.server.framework.MockContext;
import com.borqs.sync.server.framework.ServiceDescriptor;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.framework.services.http.HttpServletService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: b251
 * Date: 12/29/11
 * Time: 10:59 AM
 * Borqs project
 */
public class HttpServletService_test {
    @Test
    public void test_query_params() throws JSONException, IOException {
        HttpServletService service = new HttpServletService(new MockContext());
        service.init(getServer());
        service.start(null);

        try{
            String url = "http://localhost:8510/test_query_params?p1=a&p2=b";
            String result = httpRequest(url, null);

            JSONObject jr = new JSONObject(result);
            assertTrue(jr.has("p1"));
            assertEquals("a", jr.get("p1"));
            assertTrue(jr.has("p2"));
            assertEquals("b", jr.get("p2"));
        }finally {
            service.stop();
        }
    }

    @Test
    public void test_post_data_string() throws JSONException, IOException {
        HttpServletService service = new HttpServletService(new MockContext());
        service.init(getServer());
        service.start(null);

        try{
            String url = "http://localhost:8510/test_post_data_string?p1=a&p2=b";
            String postData = "my data by post";
            String result = httpRequest(url, postData);
            assertEquals(postData, result);
        }finally {
            service.stop();
        }
    }

    @Test
    public void test_post_data_json() throws JSONException, IOException {
        HttpServletService service = new HttpServletService(new MockContext());
        service.init(getServer());
        service.start(null);

        try{
            String url = "http://localhost:8510/test_post_data_json?p1=a&p2=b";
            String postData = "{return_name:key, return_value:value}";
            String result = httpRequest(url, postData);

            JSONObject jr = new JSONObject(result);
            assertTrue(jr.has("key"));
            assertEquals("value", jr.get("key"));
        }finally {
            service.stop();
        }
    }

    @Test
    public void test_response_writer() throws JSONException, IOException {
        HttpServletService service = new HttpServletService(new MockContext());
        service.init(getServer());
        service.start(null);

        try{
            String url = "http://localhost:8510/test_response_writer?p1=a&p2=b";
            String result = httpRequest(url, null);

            JSONObject jr = new JSONObject(result);
            assertTrue(jr.has("p1"));
            assertEquals("a", jr.get("p1"));
            assertTrue(jr.has("p2"));
            assertEquals("b", jr.get("p2"));
        }finally {
            service.stop();
        }
    }


    private ServiceDescriptor getServer() throws JSONException {
        String service = " {service:HttpServletService," +
                "   enable:true," +
                "   priority:5," +
                "   desc:test_of_http_service," +
                "   impl:MockServelet," +
                "   port:8510" +
                "  }";
        return ServiceDescriptor.from(new JSONObject(service));
    }

    private String httpRequest(String requrl, String data) throws IOException {
        URL url = new URL(requrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setDoInput(true);

        if(data != null){
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            conn.setUseCaches (false);
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            IOUtils.write(data, out);
            out.flush();
            out.close();
        } else{
            conn.setRequestMethod("GET");
        }

        InputStream is = conn.getInputStream();
        if("gzip".equals(conn.getContentEncoding())){
            is = new GZIPInputStream(is);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String line;
        while (null != (line = br.readLine())) {
            buf.append(line);
        }

        is.close();

        return buf.toString();
    }
}
