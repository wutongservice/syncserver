/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.service;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.json.JsonWriter;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.providers.ContactMapping;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.providers.ContactsMappingDao;
import com.borqs.sync.server.common.providers.IContactsMappingDao;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.contacts.mapping.logic.ContactsMappingService;
import com.borqs.sync.server.webagent.contacts.mapping.logic.IContactsMappingService;
import com.borqs.sync.server.webagent.util.DBUtility;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 5/14/12
 * Time: 1:33 PM
 * Borqs project
 */
public class ContactService {
    private Context mContext;
    private Logger mLogger;

    public ContactService(Context context){
        mContext = context;
        mLogger = WebLog.getLogger(mContext);
    }

    public long queryCount(String id){
        if(TextUtil.isEmpty(id)){
            return -1;
        }

        ContactProvider contactProvider = new ContactProvider(mContext);
        return contactProvider.countByUser(id, false);
    }


    public Map<String,String> queryRealName(String[] ids){
        if(ids == null){
            return Collections.emptyMap();
        }

        Map<String, String> name_mapping = new HashMap<String, String>();
        for(String id : ids){
            ProfileRecord profile = null;
            try {
                profile = new AccountManager(mContext)
                        .getAccountWithPrivacy("0", id, false);
            } catch (DataAccessError dataAccessError) {}
            String name = "";
            if(profile != null){
                Contact c = profile.asContact();
                name = lookupRealNameByContact(c);
                mLogger.info("real name for " + id +" is " + name);
            } else {
                mLogger.info("No profile for " + id);
            }
            name_mapping.put(id, name);
        }

        return name_mapping;
    }

    public void fetchUnconnectedContacts(String uid, ResponseWriter writer) throws AccountException {
        List<ContactMapping> unconnectedBuddy = new ArrayList<ContactMapping>();
        List<ContactMapping> contactMappings = new ArrayList<ContactMapping>();

        //1. query the buddy i know in address book
        Connection conn = mContext.getSqlConnection();
        try{
            IContactsMappingDao dao = new ContactsMappingDao(conn);
            IContactsMappingService cms = new ContactsMappingService(mContext, dao);
            contactMappings.addAll(cms.fetchMappingByOID(uid));
            WebLog.getLogger(mContext).info("Size of I know: " + contactMappings.size());
        }finally {
            DBUtility.close(conn, null, null);
        }

        //2. query the buddy know me in his address book
        conn = mContext.getSqlConnection();
        try{
            IContactsMappingDao dao = new ContactsMappingDao(conn);
            IContactsMappingService cms = new ContactsMappingService(mContext, dao);
            List<ContactMapping> knowMe = cms.fetchMappingByBID(uid);
            for(ContactMapping cm : knowMe){
                cm.setContactid(-1L); //ignore the contact id
            }
            contactMappings.addAll(knowMe);
            WebLog.getLogger(mContext).info("Size of know me: " + knowMe.size());
        }finally {
            DBUtility.close(conn, null, null);
        }

        //3. filter buddy that is not in my Wutong known SNS
        ContactProvider provider = new ContactProvider(mContext);
        for(ContactMapping cm : contactMappings){
            //check if it is connected with Contacts Address book
            String buddy_borqs_id = cm.getBorqsid();
            //make sure it not myself
            if(uid.equals(buddy_borqs_id)){
                continue;
            }
            if(!provider.isBorqsFriend(String.valueOf(cm.getContactid())) &&
                    buddy_borqs_id!=null && buddy_borqs_id.length()>0){
                unconnectedBuddy.add(cm);
            }
        }

        StringBuilder builder = new StringBuilder();
        List<String> buddies = new ArrayList<String>();
        for(ContactMapping cm : unconnectedBuddy){
            builder.append(cm.toString()).append("; ");
            buddies.add(cm.getBorqsid());
        }
        WebLog.getLogger(mContext).info("use " + uid + " have pending friends: " + unconnectedBuddy.size() +"\n " + builder.toString());

        // 4. append relation info with me
        AccountManager am = new AccountManager(mContext);
        Map<String, List<String>> relations = Collections.emptyMap();
        try {
            relations = am.getRelationWithUser(uid, buddies);
        } catch (DataAccessError dataAccessError) {
            dataAccessError.printStackTrace();
            WebLog.getLogger(mContext).info("Failed to query relation of " + uid);
        }

        try {
            JsonWriter jsonWriter = writer.asJsonWriter();
            jsonWriter.beginArray();
            for(ContactMapping cm : unconnectedBuddy){
                //ignore the buddy that exchanged card
                String bid = cm.getBorqsid();
                if(relations.containsKey(bid) && relations.get(bid).size()>0){
                    continue;
                }
                jsonWriter.beginObject()
                        .name("contact_id").value(cm.getContactid())
                        .name("borqs_id").value(cm.getBorqsid())
                        .endObject();
            }
            jsonWriter.endArray();
            jsonWriter.flush();
            jsonWriter.close();
        }catch (IOException e) {
            WebLog.getLogger(mContext).warning(e.getMessage());
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (Throwable t){
            WebLog.getLogger(mContext).warning(t.getMessage());
            t.printStackTrace(new PrintStream(writer.asStream()));
            throw AccountException.create(t);
        }
    }


    private String lookupRealNameByContact(Contact c) {
        ContactDataMiningAdapter adapter = new ContactDataMiningAdapter(mContext);

        Map<String, Integer> name_count = new HashMap<String, Integer>();

        List<ContactItem> emails = c.getEmails();
        if(emails != null){
            for(ContactItem email : emails){
                String result = adapter.lookupRealNameByEmail(email.getValue());
                countIn(result, name_count);
            }
        }


        List<ContactItem> phones = c.getTelephones();
        if(phones != null){
            for(ContactItem mobile : phones){
                String info = mobile.getValue();
                String result = adapter.lookupRealNameByMobile(mobile.getValue());
                countIn(result, name_count);
            }
        }

        //find out the first chinese name
        String final_name = "";
        Set<String> names = name_count.keySet();
        int big_count = 0;
        for(String name : names) {
            if(!isChineseName(name)){
                continue;
            }

            if(name_count.get(name) > big_count){
                final_name = name;
                big_count = name_count.get(name);
            }
        }

        return final_name;
    }

    private void countIn(String result, Map<String, Integer> name_count) {
        mLogger.info("Begin countIn");
        try {
            JSONObject jr = new JSONObject(result);
            JSONObject response = jr.getJSONObject("response");
            JSONArray docs = response.getJSONArray("docs");
            mLogger.info("countIn: Name size " + docs.length());
            for(int i=0; i<docs.length(); i++){
                JSONObject item = docs.getJSONObject(i);
                String name = item.getString("fullname");
                int count = item.getInt("count");
                if(name.length() == 0){
                    mLogger.info("countIn: no Name");
                    continue;
                }

                if(name_count.containsKey(name)){
                    count = name_count.get(name) + count;
                }
                name_count.put(name, count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLogger.info("end countIn");
    }

    boolean isChineseName(String display_name){
        final Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        for(int i=0; i<display_name.length(); i++){
            char c = display_name.charAt(i);
            Matcher chineseMatcher = p.matcher(String.valueOf(c));
            if(!chineseMatcher.find()){
                return false;
            }
        }

        if(display_name.length() <2 || display_name.length()>4){
            return false;
        }
        return true;
    }
}
