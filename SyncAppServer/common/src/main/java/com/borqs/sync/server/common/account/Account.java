package com.borqs.sync.server.common.account;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Account {
    private static final String CONFIG_ACCOUNT = "config/account_contact_field.properties";

    private static final String MATCHED_PHONE_TYPE = "matched_contact_phone_type";

    private static final String MATCHED_EMAIL_TYPE = "matched_contact_email_type";

    private static final String SYNC_APP_HOME = "sync.app.home";

    private static List<String> mPhoneFieldList = new ArrayList<String>();

    private static List<String> mEmailFieldList = new ArrayList<String>();

    static {
        Properties props = new Properties();
        try {
            String sync_app_home = System.getProperty(SYNC_APP_HOME);
            props.load(new FileInputStream(sync_app_home + "/" + CONFIG_ACCOUNT));
            String phoneFields = props.getProperty(MATCHED_PHONE_TYPE);
            String emailFields = props.getProperty(MATCHED_EMAIL_TYPE);

            String[] phoneArray = phoneFields.split(",");
            if (phoneArray != null && phoneArray.length > 0) {
                mPhoneFieldList = Arrays.asList(phoneArray);
            }

            String[] emailArray = emailFields.split(",");
            if (emailArray != null && emailArray.length > 0) {
                mEmailFieldList = Arrays.asList(emailArray);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param field
     * @return true if the specified field(type) belongs to the configured phone
     *         type
     */
    public static boolean containsPhone(String field) {
        return mPhoneFieldList.contains(field);
    }

    /**
     * @param field
     * @return true if the specified field(type) belongs to the configured email
     *         type
     */
    public static boolean containsEmail(String field) {
        return mEmailFieldList.contains(field);
    }
}
