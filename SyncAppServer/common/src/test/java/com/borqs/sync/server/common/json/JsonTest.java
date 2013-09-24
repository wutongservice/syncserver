package com.borqs.sync.server.common.json;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: b251
 * Date: 2/6/13
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonTest {

    @Test
    public void test() throws JSONException {
        JSONObject o1 = new JSONObject();
        {
        o1.put("key1", "value1");
        JSONObject o2 = new JSONObject();
        o2.put("key21", "value21");
        o2.put("key22", "value22");

        o1.put("key2", o2);
        }

        System.out.println(o1.toString());
        JSONObject t1 = new JSONObject(o1.toString());
        {
            String v1 = t1.getString("key1");
            String v2 = t1.getString("key2");
            JSONObject vo2 = t1.getJSONObject("key2");

            System.out.println(v1);
            System.out.println(v2);
            System.out.println(vo2.toString());

            JSONObject o3 = new JSONObject(v2);
            String v21 = o3.getString("key21");
            String v22 = o3.getString("key22");
            System.out.println(v21);
            System.out.println(v22);
        }

    }

    @Test
    public void test2() throws UnsupportedEncodingException, JSONException {
        String data = "%7B%22guid%22%3A%2251c30f96-84ca-4a39-8bbb-08408958d80c%22%2C%22fieldandvalue%22%3A%22%7B%5C%22contact_info%5C%22%3A%7B%5C%22mobile_telephone_number%5C%22%3A%5C%221213213422%5C%22%2C%5C%22email_address%5C%22%3A%5C%22chenxt_test2%40126.com%5C%22%7D%2C%5C%22password%5C%22%3A%5C%2200504EC731F1939988A6010A2630D827%5C%22%2C%5C%22display_name%5C%22%3A%5C%22tddfg%5C%22%7D%22%7D";

        String str = URLDecoder.decode(data, "utf-8");
        System.out.println(str);

        JSONObject o1 = new JSONObject(str);
        String s1 = o1.getString("fieldandvalue");
        System.out.println(s1);

        JSONObject o2 = new JSONObject(s1);
        System.out.println(o2.getString("contact_info"));
        System.out.println(o2.getJSONObject("contact_info").getString("mobile_telephone_number"));
    }

}
