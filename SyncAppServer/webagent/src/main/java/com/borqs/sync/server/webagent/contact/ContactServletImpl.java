package com.borqs.sync.server.webagent.contact;

import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.dao.ContactDAO;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 7/31/12
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactServletImpl {

    //ContactServletImpl.queryGuidByLuid(mContext, userid,device,luids, writer);
    public static void queryGuidByLuid(Context context, String userid, String device,
                                       String luids, ResponseWriter writer) throws JSONException, IOException {
        ContactDAO dao = new ContactDAO(context);
        Map<String,String> luidGuidMapping = dao.getLuidGuidMapping(luids, userid, device);

        Set<String> luidSet = luidGuidMapping.keySet();
        // response:[{"luid":"0:100","guid":"65150"},{"luid":"0:101","guid":"65151"},
        // {"luid":"0:102","guid":"65152"}]
        JSONArray  mappingArray = new JSONArray();
        for(String luid:luidSet){
            JSONObject mappingObj = new JSONObject();
            mappingObj.put("luid", luid);
            mappingObj.put("guid", luidGuidMapping.get(luid));
            mappingArray.put(mappingObj);
        }
        ResponseWriterUtil.writeObjectJson(mappingArray.toString(),writer);
    }
}
