package com.borqs.sync.server.common.datamining;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 6/13/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationProfile {

    private List<IntegrationProfileName> mNames;
    private List<IntegrationProfileItem> mPhones;
    private List<IntegrationProfileAddress> mAddresses;
    private List<IntegrationProfileItem> mWebs;
    private List<IntegrationProfileItem> mEmails;
    private List<IntegrationProfileItem> mIms;
    
    public IntegrationProfile(){
        mNames = new ArrayList<IntegrationProfileName>();
        mPhones = new ArrayList<IntegrationProfileItem>();
        mAddresses = new ArrayList<IntegrationProfileAddress>();
        mWebs = new ArrayList<IntegrationProfileItem>();
        mEmails = new ArrayList<IntegrationProfileItem>();
        mIms = new ArrayList<IntegrationProfileItem>();
    }

    public void addName(IntegrationProfileName name){
        mNames.add(name);
    }

    public void setNames(List<IntegrationProfileName> names){
        mNames = names;
    }

    public List<IntegrationProfileName> getNames(){
        return mNames;
    }

    public void setPhones(List<IntegrationProfileItem> phones){
        mPhones = phones;
    }
    
    public List<IntegrationProfileItem> getPhones(){
        return mPhones;
    }

    public void addPhone(IntegrationProfileItem phone){
        mPhones.add(phone);
    }

    public void setAddresses(List<IntegrationProfileAddress> addresses){
        mAddresses = addresses;
    }

    public void addAddress(IntegrationProfileAddress address){
        mAddresses.add(address);
    }

    public List<IntegrationProfileAddress> getAddresses(){
        return mAddresses;
    }

    public void setWebs(List<IntegrationProfileItem> webs){
        mWebs = webs;
    }

    public void addWeb(IntegrationProfileItem web){
        mWebs.add(web);
    }
    
    public List<IntegrationProfileItem> getWebs(){
        return mWebs;
    }

    public void setEmails(List<IntegrationProfileItem> emails){
        mEmails = emails;
    }

    public void addEmail(IntegrationProfileItem email){
        mEmails.add(email);
    }
    
    public List<IntegrationProfileItem> getEmails(){
        return mEmails;
    }

    public void setIms(List<IntegrationProfileItem> ims){
        mIms = ims;
    }

    public List<IntegrationProfileItem> getIms(){
        return mIms;
    }

    public void addIm(IntegrationProfileItem im){
        mIms.add(im);
    }


}
