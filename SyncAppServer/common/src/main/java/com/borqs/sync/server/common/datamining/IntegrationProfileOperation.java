package com.borqs.sync.server.common.datamining;

import com.borqs.sync.server.common.json.JsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/24/12
 * Time: 1:55 PM
 *  * {
 "_id":{
 "$id":"4fb5b58e5f85c4c605a737ca"
 },
 "userId":[
 "10043",
 "10046",
 "17",
 "10226",
 "10364",
 "10362",
 "10408",
 "10222",
 "10425",
 "10014",
 "10015",
 "10392",
 "10016",
 "10010",
 "199",
 "10012",
 "10178",
 "10040",
 "10041",
 "10056",
 "10259",
 "10055",
 "10033",
 "10255",
 "230",
 "10212",
 "10358",
 "10027",
 "10001",
 "10498",
 "10025",
 "10439",
 "10000",
 "10005",
 "10006",
 "5",
 "10288",
 "10004",
 "10009",
 "10008",
 "233",
 "235"
 ],
 "borqsId":"b10010",
 "names":[{"count":1,"firstName":"余, 亭 雪"},{"count":1,"firstName":"雪亭","lastName":"余"},{"count":5,"firstName":"余雪亭"},{"count":51,"firstName":"亭","middleName":"雪","lastName":"余"}],
 "firstname":{
 "通":41,
 "陈学通":6
 },
 "middleName":{
 "学":41
 },
 "lastname":{
 "陈":41
 },
 "displayName":{
 "":2,
 "陈":2,
 "陈学通":1
 },
 "nickName":[

 ],
 "birthday":[

 ],
 "anniversary":[

 ],
 "hobbies":[

 ],
 "title":[

 ],
 "assistant":[

 ],
 "company":{
 "播思":47
 },
 "department":[

 ],
 "jobTitle":{
 "资深软件工程师":3
 },
 "officeLocation":[

 ],
 "profession":[

 ],
 "gender":{
 "m":42
 },
 "lastUpdate":1337308559223,
 "phones":[
 {
 "type":3,
 "value":"13910912321",
 "count":47
 }
 ],
 "mails":[
 {
 "type":4,
 "value":"xuetong.chen@borqs.com",
 "count":48,
 "private":0,
 "lastUpdate":0
 },
 {
 "type":16,
 "value":"chenxt.borqs@gmail.com",
 "count":48,
 "private":0,
 "lastUpdate":0
 },
 {
 "type":23,
 "value":"xuetong3.chen@borqs.com",
 "count":1,
 "private":1,
 "lastUpdate":1337244550778
 },
 {
 "type":16,
 "value":"chenxt.cn@gmail.com",
 "count":1,
 "private":0,
 "lastUpdate":0
 },
 {
 "type":23,
 "value":"xuetongchen@yahoo.com",
 "count":1,
 "private":0,
 "lastUpdate":0
 },
 {
 "type":23,
 "value":"xuetong5.chen@borqs.com",
 "count":1,
 "private":1,
 "lastUpdate":1337244550778
 }
 ],
 "ims":[

 ],
 "addresses":[{"type":3,"street":"天通苑","city":"","state":"","postcode":"","pobox":"","extAddr":"","country":"","count":1,"private":0,"lastUpdate":0}]
 }
 */
public class IntegrationProfileOperation {

    //integration profile constant
    public static final String INTEGRATION_COUNT = "count";
    public static final String INTEGRATION_PROFILE_FIRST_NAME = "firstName";
    public static final String INTEGRATION_PROFILE_MIDDLE_NAME = "middleName";
    public static final String INTEGRATION_PROFILE_LAST_NAME = "lastName";
    public static final String INTEGRATION_PROFILE_BFIRST_NAME = "bfirstName";
    public static final String INTEGRATION_PROFILE_BMIDDLE_NAME = "bmiddleName";
    public static final String INTEGRATION_PROFILE_BLAST_NAME = "blastName";
//    public static final String INTEGRATION_PROFILE_COMPANY = "company";
//    public static final String INTEGRATION_PROFILE_JOBTITLE = "jobTitle";
//    public static final String INTEGRATION_PROFILE_GENDER = "gender";
    public static final String INTEGRATION_PROFILE_ITEM_PHONES = "phones";
    public static final String INTEGRATION_PROFILE_ITEM_MAILS = "mails";
    public static final String INTEGRATION_PROFILE_ITEM_IMS = "ims";
    public static final String INTEGRATION_PROFILE_ITEM_WEBS = "webs";
    public static final String INTEGRATION_PROFILE_ITEM_ADDRESSES = "addresses";
    public static final String INTEGRATION_PROFILE_ITEM_NAMES = "allnames";

    public static final String INTEGRATION_PROFILE_ADDRESSES_CITY_KEY = "city";
    public static final String INTEGRATION_PROFILE_ADDRESSES_STATE_KEY = "state";
    public static final String INTEGRATION_PROFILE_ADDRESSES_POST_OFFICE_ADDRESS_KEY = "pobox";
    public static final String INTEGRATION_PROFILE_ADDRESSES_POSTAL_CODE_KEY = "postcode";
    public static final String INTEGRATION_PROFILE_ADDRESSES_STREET_KEY = "street";
    public static final String INTEGRATION_PROFILE_ADDRESSES_EXTENDED_ADDRESS_KEY = "extAddr";
    public static final String INTEGRATION_PROFILE_ADDRESSES_COUNTRY_KEY = "country";

//    List<ContactField> mContactFieldList = new ArrayList<ContactField>();

    private IntegrationProfile mProfile;

    public IntegrationProfileOperation(){
        mProfile = new IntegrationProfile();
    }

    public IntegrationProfile getProfile(){
        return mProfile;
    }
    
    public void parseName(JsonReader reader) throws IOException {
        reader.beginArray();
        while(reader.hasNext()){
            IntegrationProfileName name = parseOneNameItem(reader);
            mProfile.addName(name);
        }
        reader.endArray();
    }

    private IntegrationProfileName parseOneNameItem(JsonReader reader) throws IOException {
        int count = 0;
        IntegrationProfileName item = new IntegrationProfileName();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(INTEGRATION_PROFILE_FIRST_NAME.equals(name)){
                item.setFirstName(reader.nextString());
            }else if(INTEGRATION_PROFILE_MIDDLE_NAME.equals(name)){
                item.setMiddleName(reader.nextString());
            }else if(INTEGRATION_PROFILE_LAST_NAME.equals(name)){
                item.setLastName(reader.nextString());
            }else if(INTEGRATION_PROFILE_BFIRST_NAME.equals(name)){
                item.setBFirstName(reader.nextString());
            }else if(INTEGRATION_PROFILE_BMIDDLE_NAME.equals(name)){
                item.setBMiddleName(reader.nextString());
            }else if(INTEGRATION_PROFILE_BLAST_NAME.equals(name)){
                item.setBLastName(reader.nextString());
            }else if(INTEGRATION_COUNT.equals(name)){
                item.setCount(reader.nextInt());
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return item;


    }

    public void parseAddressField(JsonReader reader) throws IOException {
        List<IntegrationProfileAddress> items = new ArrayList<IntegrationProfileAddress>();
        reader.beginArray();
        while(reader.hasNext()){
            IntegrationProfileAddress address = parseOneAddressItem(reader);
            items.add(address);
        }
        reader.endArray();
        mProfile.setAddresses(items);
    }

    private IntegrationProfileAddress parseOneAddressItem(JsonReader reader) throws IOException {
        int count = 0;
        IntegrationProfileAddress address = new IntegrationProfileAddress();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(INTEGRATION_PROFILE_ADDRESSES_STREET_KEY.equals(name)){
                address.setStreet(reader.nextString());
            }else if (INTEGRATION_PROFILE_ADDRESSES_CITY_KEY.equals(name)){
                address.setCity(reader.nextString());
            }else if(INTEGRATION_PROFILE_ADDRESSES_STATE_KEY.equals(name)){
                address.setState(reader.nextString());
            }else if(INTEGRATION_PROFILE_ADDRESSES_POSTAL_CODE_KEY.equals(name)){
                address.setPostcode(reader.nextString());
            }else if(INTEGRATION_PROFILE_ADDRESSES_POST_OFFICE_ADDRESS_KEY.equals(name)){
                address.setPostOfficeAddress(reader.nextString());
            }else if(INTEGRATION_PROFILE_ADDRESSES_EXTENDED_ADDRESS_KEY.equals(name)){
                address.setExtendedAddress(reader.nextString());
            }else if(INTEGRATION_PROFILE_ADDRESSES_COUNTRY_KEY.equals(name)){
                address.setCountry(reader.nextString());
            }else if(INTEGRATION_COUNT.equals(name)){
                address.setCount(reader.nextInt());
            }else if("private".equals(name)){
                address.setPrivate(1 == reader.nextInt());
            }else if("lastUpdate".equals(name)){
                address.setLastUpdate(reader.nextLong());
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return address;
    }

    public void parsePhones(JsonReader reader) throws IOException {
        mProfile.setPhones(parseItemField(reader));
    }

    public void parseEmails(JsonReader reader) throws IOException {
        mProfile.setEmails(parseItemField(reader));
    }

    public void parseIMs(JsonReader reader) throws IOException {
        mProfile.setIms(parseItemField(reader));
    }

    public void parseWebs(JsonReader reader) throws IOException {
        mProfile.setWebs(parseItemField(reader));
    }

    private List<IntegrationProfileItem> parseItemField(JsonReader reader) throws IOException {
        List<IntegrationProfileItem> items = new ArrayList<IntegrationProfileItem>();

        reader.beginArray();
        while(reader.hasNext()){
            IntegrationProfileItem contactItem = parseOneItem(reader);
            items.add(contactItem);
        }
        reader.endArray();

        return items;
    }
    
    private IntegrationProfileItem parseOneItem(JsonReader reader) throws IOException {
        int count = 0;
        IntegrationProfileItem item = new IntegrationProfileItem();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if("type".equals(name)){
                item.setType(reader.nextInt());
            }else if("value".equals(name)){
                item.setValue(reader.nextString());
            }else if("private".equals(name)){
                item.setIsPrivate(1 == reader.nextInt());
            }else if("lastUpdate".equals(name)){
                item.setLastUpdate(reader.nextLong());
            }else if(INTEGRATION_COUNT.equals(name)){
                item.setCount(reader.nextInt());
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return item;
    }

}
