/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.contact.manager;

import com.borqs.pim.jcontact.JAddress;
import com.borqs.pim.jcontact.JContact;
import com.borqs.pim.jcontact.JContactBuilder;
import com.borqs.pim.jcontact.JORG;
import com.borqs.sync.server.contact.MergeUtils;
import com.funambol.framework.tools.merge.MergeResult;
import com.funambol.json.converter.Converter;
import com.funambol.json.dao.JsonDAO;
import com.funambol.json.domain.JsonItem;
import com.funambol.json.exception.DaoException;
import com.funambol.json.exception.HttpException;
import com.funambol.json.exception.MalformedJsonContentException;
import com.funambol.json.manager.JsonManagerImpl;

import java.util.List;
import java.util.TimeZone;

/**
 * Date: 3/12/12
 * Time: 5:07 PM
 * Borqs project
 */
public class JContactManager extends JsonManagerImpl<JContact> {
    public JContactManager(JsonDAO dao, Converter<JsonItem<JContact>> jsonItemConverter) {
        super(dao, jsonItemConverter);
    }

        @Override
    public boolean mergeExtendedItem(String sessionID, JsonItem<JContact> serverItem, JsonItem<JContact> clientItem, long since) throws DaoException, MalformedJsonContentException {
        try {

            // client item
            JContact serverContact = (JContact) serverItem.getItem();
            JContact clientContact = (JContact) clientItem.getItem();

            MergeResult mergeResult = merge(clientContact, serverContact);

            if (log.isTraceEnabled()) {
                log.trace("Merge procedure end. MergeResult: " + mergeResult);
            }

//            if (mergeResult.isSetBRequired()) {
//                updateExtendedItem(sessionID, serverItem, since);
//            }

            return /*mergeResult.isSetARequired()*/true;//default use server if conflict

        } catch (HttpException re) {
            log.error("Failed the connection to the Json backend", re);
            throw new DaoException(re.getMessage(), re);
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
            throw new MalformedJsonContentException("The Json content is malformed!", re);
        }
    }

    @Override
    public boolean mergeRFCItem(String sessionID, JsonItem<JContact> serverItem, JsonItem<JContact> clientItem, long since, boolean vcardIcalBackend, boolean vcalFormat, String rfcType, TimeZone timezone, String charset) throws DaoException, MalformedJsonContentException {
        return false;
    }

    @Override
    public boolean isTwinSearchAppliableOn(JsonItem<JContact> jContactJsonItem) {
        JContact jc = jContactJsonItem.getItem();
        //name
        boolean hasName = !isEmpty(jc.getFirstName()) ||
                !isEmpty(jc.getLastName()) ||
                !isEmpty(jc.getMiddleName()) ||
                !isEmpty(jc.getNamePostfix()) ||
                !isEmpty(jc.getNamePrefix()) ||
                !isEmpty(jc.getFirstNamePinyin()) ||
                !isEmpty(jc.getMiddleNamePinyin()) ||
                !isEmpty(jc.getLastNamePinyin()) ||
                !isEmpty(jc.getNickName());
        //email
        List<JContact.TypedEntity> emailList = jc.getEmailList();
        boolean hasEmail = false;
        if(emailList != null){
            for(JContact.TypedEntity email:emailList){
                if(email != null && email.getValue() != null && String.valueOf(email.getValue()).length() > 0){
                    hasEmail = true;
                    break;
                }
            }
        }

        //phone
        List<JContact.TypedEntity> phoneList = jc.getPhoneList();
        boolean hasPhone = false;
        if(emailList != null){
            for(JContact.TypedEntity phone:phoneList){
                if(phone != null && phone.getValue() != null && String.valueOf(phone.getValue()).length() > 0){
                    hasPhone = true;
                    break;
                }
            }
        }

        //address
        List<JContact.TypedEntity> addressList = jc.getAddressList();
        boolean hasAddress = false;
        if(addressList != null){
            for(JContact.TypedEntity address:addressList){
                if(address != null){
                    hasAddress = !isEmpty(JAddress.city(address.getValue()))||
                            !isEmpty(JAddress.province(address.getValue()))||
                            !isEmpty(JAddress.street(address.getValue()))||
                            !isEmpty(JAddress.zipcode(address.getValue()));
                    if(hasAddress){
                        break;
                    }
                }
            }
        }

        //im
        List<JContact.TypedEntity> imList = jc.getIMList();
        boolean hasIM = false;
        if(imList != null){
            for(JContact.TypedEntity im:imList){
                if(im != null && im.getValue() != null && String.valueOf(im.getValue()).length() > 0){
                    hasIM = true;
                    break;
                }
            }
        }

        //webpage
        List<JContact.TypedEntity> webList = jc.getWebpageList();
        boolean hasWeb = false;
        if(webList != null){
            for(JContact.TypedEntity web:webList){
                if(web != null && web.getValue() != null && String.valueOf(web.getValue()).length() > 0){
                    hasIM = true;
                    break;
                }
            }
        }

        //birthday
        boolean hasBirthday = isEmpty(jc.getBirthday());

        //org
        List<JContact.TypedEntity>  orgList = jc.getOrgList();
        boolean hasOrg = false;
        if(orgList != null){
            for(JContact.TypedEntity org:orgList){
                if(org != null){
                    hasOrg = !isEmpty(JORG.company(org.getValue()))||
                            !isEmpty(JORG.title(org.getValue()));
                    if(hasOrg){
                        break;
                    }
                }
            }
        }

        //photo
        boolean hasPhoto;
        byte[] photo = jc.getPhoto();
        hasPhoto = (photo != null);

        //TODO refer to JsonContactManager to add more
        return hasName || hasEmail || hasPhone || hasPhoto || hasAddress || hasIM || hasWeb || hasBirthday || hasOrg;
    }

    private MergeResult merge(JContact client, JContact server){

        //TODO
//        MergeResult contactMergeResult = new MergeResult("Contact");
//
//        if (server == null) {
//            throw new IllegalStateException("The given contact must not be null");
//        }
//
//        MergeResult result = null; // temporary object used to store the merge
//        // result for each field
//
//        //Name
//        String serverFirstName = server.getFirstName() == null?"":server.getFirstName().trim();
//        String  serverMiddleName = server.getMiddleName() == null?"":server.getMiddleName().trim();
//        String  serverLastName = server.getLastName() == null?"":server.getLastName().trim();
//        String serverName = serverLastName + serverMiddleName + serverFirstName;
//
//        String clientFirstName = server.getFirstName() == null?"":server.getFirstName().trim();
//        String clientMiddleName = server.getMiddleName() == null?"":server.getMiddleName().trim();
//        String clientLastName = server.getLastName() == null?"":server.getLastName().trim();
//        String clientName = clientLastName + clientMiddleName + clientFirstName;
//
//
//        // Languages
//        result = MergeUtils.compareStrings(clientLastName, serverName);
//        if (result.isSetARequired()) {
//            //this.setLanguages(otherLanguages);
//            JContactBuilder builder = new JContactBuilder();
//        } else if (result.isSetBRequired()) {
//            otherContact.setLanguages(myLanguages);
//        }
//        contactMergeResult.addMergeResult(result, "Languages");
//
//        // Name
//        Name otherName = otherContact.getName();
//        if (this.name == null) {
//            if (otherName != null) {
//                this.name = otherName;
//                contactMergeResult.addPropertyA("Name");
//            }
//        } else {
//            result = this.name.merge(otherName);
//            contactMergeResult.addMergeResult(result,"Name");
//        }
//
//        // XTags
//        /* @TODO */
//
//        // Notes
//        if (otherContact.getNotes() != null || notes != null) {
//            if (otherContact.getNotes() == null) {
//                otherContact.setNotes(new ArrayList());
//            }
//            if (notes == null) {
//                notes = new ArrayList<Note>();
//            }
//            result = PDIMergeUtils.mergeTypifiedPropertiestList(notes,
//                    otherContact.getNotes());
//            contactMergeResult.addMergeResult(result, "Notes");
//        }
//
//        // PersonalDetail
//        result = personalDetail.merge(otherContact.personalDetail);
//        contactMergeResult.addMergeResult(result, "PersonalDetail");
//
//        // BusinessDetail
//        result = businessDetail.merge(otherContact.businessDetail);
//        contactMergeResult.addMergeResult(result, "BusinessDetail");
//
//        return contactMergeResult;



        return null;
    }
    
    private boolean isEmpty(String str){
        return str==null||str.isEmpty();
    }
}
