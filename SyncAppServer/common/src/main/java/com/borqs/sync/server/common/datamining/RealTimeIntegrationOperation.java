package com.borqs.sync.server.common.datamining;

import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.common.util.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 6/29/12
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RealTimeIntegrationOperation {
    
    private static final String SQL_GET_ALL_CONTACTS_BY_BORQSID = "select id from borqs_pim_contact where borqsid=?";
    
    private Context mContext;
    private Logger mLogger;
    private ContactProvider mContactProvider;

    public RealTimeIntegrationOperation(Context context){
        mContext = context;
        mContactProvider = new ContactProvider(context);

    }

    public void setLogger(Logger logger){
        mLogger = logger;
        mContactProvider.useLogger(mLogger);
    }

    public IntegrationProfile generateIntegrationProfile(String borqsId){
        IntegrationProfile profile = new IntegrationProfile();
        List<Long> ids = getAllContactsByBorqsId(borqsId);
        for(long id:ids){
            Contact contact = mContactProvider.getItem(String.valueOf(id));
            profile = mergeProfile(profile,contact);
        }
        return profile;
    }

    private List<Long> getAllContactsByBorqsId(String borqsId){
        List<Long> ids = new ArrayList<Long>();
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_GET_ALL_CONTACTS_BY_BORQSID);
            ps.setString(1,borqsId);

            rs = ps.executeQuery();
            while(rs.next()){
                long contactId = rs.getLong(1);
                LogHelper.logInfo(mLogger,"contact:" + contactId + " has borqsid :" + borqsId);
                ids.add(contactId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DBUtility.close(conn,ps,rs);
        }
        return ids;
    }

    private IntegrationProfile mergeProfile(IntegrationProfile srcProfile,Contact contact){
        //merge name
        srcProfile = mergeName(srcProfile,contact);
        //merge phones
        srcProfile.setPhones(mergePhones(srcProfile, contact));
        //merge emails
        srcProfile.setEmails(mergeEmails(srcProfile, contact));
        //merge webs
        srcProfile.setWebs(mergeWebs(srcProfile, contact));
        //merge ims
        srcProfile.setIms(mergeIms(srcProfile, contact));
        //mergeAddress
        srcProfile.setAddresses(mergeAddress(srcProfile,contact));
        return srcProfile;
    }

    private List<IntegrationProfileItem> mergePhones(IntegrationProfile srcProfile,Contact contact){
        return mergeItem(srcProfile.getPhones(),contact.getTelephones());
    }

    private List<IntegrationProfileItem> mergeEmails(IntegrationProfile srcProfile,Contact contact){
        return mergeItem(srcProfile.getEmails(),contact.getEmails());
    }

    private List<IntegrationProfileItem> mergeIms(IntegrationProfile srcProfile,Contact contact){
        return mergeItem(srcProfile.getIms(),contact.getIms());
    }

    private List<IntegrationProfileItem> mergeWebs(IntegrationProfile srcProfile,Contact contact){
        return mergeItem(srcProfile.getWebs(),contact.getWebpages());
    }

    private List<IntegrationProfileAddress> mergeAddress(IntegrationProfile srcProfile,Contact contact){
        List<IntegrationProfileAddress> newAddresses = new ArrayList<IntegrationProfileAddress>();
        List<IntegrationProfileAddress> integrationProfileAddresses = srcProfile.getAddresses();
        List<Address> contactAddresses = contact.getAddress();

        if(contactAddresses != null){
            for(Address contactAddress:contactAddresses){
                boolean found = false;
                if(integrationProfileAddresses != null){
                    for(IntegrationProfileAddress integrationProfileAddress:integrationProfileAddresses){
                        if(Utility.stringEqual(integrationProfileAddress.toValue(),contactAddress.toValue())){
                            found = true;
                            integrationProfileAddress.setCount(integrationProfileAddress.getCount() + 1);
                            break;
                        }
                    }
                }
                if(!found){
                    IntegrationProfileAddress newAddress = new IntegrationProfileAddress();
                    newAddress.setCount(1);
                    newAddress.setExtendedAddress(contactAddress.getExtendedAddress());
                    newAddress.setCity(contactAddress.getCity());
                    newAddress.setCountry(contactAddress.getCountry());
                    newAddress.setLastUpdate(contactAddress.getLastUpdate());
                    newAddress.setPostcode(contactAddress.getPostalCode());
                    newAddress.setPostOfficeAddress(contactAddress.getPostOfficeAddress());
                    newAddress.setPrivate(contactAddress.isPrivate());
                    newAddress.setState(contactAddress.getState());
                    newAddress.setStreet(contactAddress.getStreet());
                    newAddresses.add(newAddress);
                }
            }
            for(IntegrationProfileAddress newItem:newAddresses){
                integrationProfileAddresses.add(newItem);
            }
        }
        return integrationProfileAddresses;
    }

    private List<IntegrationProfileItem> mergeItem(List<IntegrationProfileItem> integrationItems, List<ContactItem> contactItems) {
        List<IntegrationProfileItem> newItems = new ArrayList<IntegrationProfileItem>();
        if(contactItems != null){
            for(ContactItem contactItem:contactItems){
                boolean found = false;
                if(integrationItems != null){
                    for(IntegrationProfileItem integrationProfileItem:integrationItems){
                        if(integrationProfileItem.getType() == contactItem.getType()
                                && Utility.stringEqual(integrationProfileItem.getValue(),contactItem.getValue())
                                && integrationProfileItem.isPrivate() == contactItem.isPrivate()
                                && integrationProfileItem.getLastUpdate() == contactItem.getLastUpdate()){
                            integrationProfileItem.setCount(integrationProfileItem.getCount() + 1);
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    IntegrationProfileItem newItem = new IntegrationProfileItem();
                    newItem.setValue(contactItem.getValue());
                    newItem.setCount(1);
                    newItem.setType(contactItem.getType());
                    newItem.setIsPrivate(contactItem.isPrivate());
                    newItem.setLastUpdate(contactItem.getLastUpdate());
                    newItems.add(newItem);
                }
            }
            for(IntegrationProfileItem item:newItems){
                integrationItems.add(item);
            }
        }
        return integrationItems;
    }

    private IntegrationProfile mergeName(IntegrationProfile srcProfile,Contact contact){
        String contactFirstName = contact.getFirstName()==null?"":contact.getFirstName().trim();
        String contactMiddleName = contact.getMiddleName()==null?"":contact.getMiddleName().trim();
        String contactLastName = contact.getLastName()==null?"":contact.getLastName().trim();
        String contactBFirstName = contact.getBFirstName()==null?"":contact.getBFirstName().trim();
        String contactBMiddleName = contact.getBMiddleName()==null?"":contact.getBMiddleName().trim();
        String contactBLastName = contact.getBLastName()==null?"":contact.getBLastName().trim();
        String contactName = contactFirstName + contactMiddleName + contactLastName + contactBFirstName + contactBMiddleName + contactBLastName;

        List<IntegrationProfileName> names = srcProfile.getNames();
        boolean found = false;

        if(names != null){
            for(IntegrationProfileName name:names){
                String integrationFirstName = name.getFirstName()==null?"":name.getFirstName().trim();
                String integrationMiddleName = name.getMiddleName()==null?"":name.getMiddleName().trim();
                String integrationLastName = name.getLastName()==null?"":name.getMiddleName().trim();
                String integrationBFirstName = name.getBFirstName()==null?"":name.getBFirstName().trim();
                String integrationBMiddleName = name.getBMiddleName()==null?"":name.getBMiddleName().trim();
                String integrationBLastName = name.getBLastName()==null?"":name.getBMiddleName().trim();
                String integrationName = integrationFirstName + integrationMiddleName + integrationLastName + integrationBFirstName + integrationBMiddleName + integrationBLastName;
                if(contactName.equalsIgnoreCase(integrationName)){
                    found = true;
                    name.setCount(name.getCount() + 1);
                    break;
                }
            }
        }
        if(!found){
            IntegrationProfileName newName = new IntegrationProfileName();
            newName.setFirstName(contactFirstName);
            newName.setMiddleName(contactMiddleName);
            newName.setLastName(contactLastName);
            newName.setBFirstName(contactBFirstName);
            newName.setBMiddleName(contactBMiddleName);
            newName.setBLastName(contactBLastName);
            newName.setCount(1);
            names.add(newName);
        }
        srcProfile.setNames(names);
        return srcProfile;
    }


}
