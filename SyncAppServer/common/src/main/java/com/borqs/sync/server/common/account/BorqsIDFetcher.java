package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.json.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class BorqsIDFetcher {
	private static Logger log = Logger.getLogger(BorqsIDFetcher.class);
	
	private static final String GET_BORQSID_ADDR = "/phonebook/look_up";
	
//	private static HttpClient client = new DefaultHttpClient();

	synchronized public static String fetchBorqsIDs(String url, String request) {
		HttpClient client = new DefaultHttpClient();
		
		String result = null;
		HttpPost httpPost = new HttpPost(url+GET_BORQSID_ADDR);
		StringEntity entity = null;
		HttpEntity resEntity = null;
		
		try {
//			BasicHttpParams postParams = new BasicHttpParams();
//			postParams.setParameter("contact_info", request);
//			httpPost.setParams(postParams);
			
			entity = new StringEntity("contact_info="+request);
			entity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(entity);
			
			HttpResponse response = client.execute(httpPost);
			resEntity = new GzipDecompressingEntity(response.getEntity());
			result = EntityUtils.toString(resEntity);
			result = result.replace(" ", "")
					.replace(System.getProperty("line.separator"), "");
		} catch (ClientProtocolException e) {
			log.error("failed to get BorqsIDs!", e);
		} catch (IOException e) {
			log.error("failed to get BorqsIDs!", e);
		} finally {
			if(null != resEntity) {
				try {
					EntityUtils.consume(resEntity);
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}

			if(!httpPost.isAborted()) {
				httpPost.abort();
			}
		}
		
		return result;
	}
	
	/**
	 * convert JSON to Java object
	 * @param entity
	 * @return
	 */
	public static Map<String, String> fromJSON(String entity) {
		Map<String, String> results = new HashMap<String, String>();
		if(null == entity || "".equals(entity)) {
			return results;
		}
		
		JsonReader reader = new JsonReader(new StringReader(entity));
		try {
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				String mail = null, phone = null, userId = null;
				while (reader.hasNext()) {
					 String name = reader.nextName();
					 if("email".equalsIgnoreCase(name)) {
						 mail = reader.nextString();
					 } else if("phone".equalsIgnoreCase(name)) {
						 phone = reader.nextString();
					 } else if("user_id".equalsIgnoreCase(name)) {
						 userId = reader.nextString();
					 } else {
						 reader.skipValue();
					 }
				}
				reader.endObject();
				if(mail != null) {
					results.put("m-"+mail, userId);
				}
				if(phone != null) {
					results.put("p-"+phone, userId);
				}
			}
			reader.endArray();
		} catch(Exception e) {
			log.error("failed convert JSON to object->"+entity, e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
}
