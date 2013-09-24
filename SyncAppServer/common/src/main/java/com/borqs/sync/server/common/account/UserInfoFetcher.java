package com.borqs.sync.server.common.account;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.borqs.sync.server.common.json.JsonReader;

public class UserInfoFetcher {
	private static Logger log = Logger.getLogger(UserInfoFetcher.class);
	
	private static final String GET_USER = "/internal/getUsers?viewerId=41&cols=login_phone1,login_phone2,login_phone3,login_email1,login_email2,login_email3&privacyEnabled=false&userIds=";
	private static HttpClient client = new DefaultHttpClient();
	
	public static UserInfo fetchUserInfo(String address, String userId) {
		//String url = Config.getInstance().getUserUrl() + userId;
		HttpGet httpGet = new HttpGet(address+GET_USER+userId);

		String entity = "[]";
		UserInfo userInfo = new UserInfo();
		
		try {
			HttpResponse response = client.execute(httpGet);
			GzipDecompressingEntity gzipDecompressingEntity = new GzipDecompressingEntity(response.getEntity());
			entity = EntityUtils.toString(gzipDecompressingEntity);
			
			JsonReader reader = new JsonReader(new StringReader(entity));
			try {
				reader.beginArray();
				while (reader.hasNext()) {
					
					reader.beginObject();
					while (reader.hasNext()) {
						 String name = reader.nextName();
						 if("user_id".equalsIgnoreCase(name)) {
							 userInfo.setUserId(reader.nextString());
						 } else if("login_email1".equalsIgnoreCase(name)) {
							 userInfo.setLogin_email1(reader.nextString());
						 } else if("login_email2".equalsIgnoreCase(name)) {
							 userInfo.setLogin_email2(reader.nextString());
						 } else if("login_email3".equalsIgnoreCase(name)) {
							 userInfo.setLogin_email3(reader.nextString());
						 } else if("login_phone1".equalsIgnoreCase(name)) {
							 userInfo.setLogin_phone1(reader.nextString());
						 } else if("login_phone2".equalsIgnoreCase(name)) {
							 userInfo.setLogin_phone2(reader.nextString());
						 } else if("login_phone3".equalsIgnoreCase(name)) {
							 userInfo.setLogin_phone3(reader.nextString());
						 } else {
							 reader.skipValue();
						 }
					}
					reader.endObject();
				}
				reader.endArray();
			} catch(Exception e) {
				log.error("failed convert JSON to object->"+entity, e);
				httpGet.abort();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
//				EntityUtils.consume(gzipDecompressingEntity);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpGet.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpGet.abort();
		}

		return userInfo;
	}
}
