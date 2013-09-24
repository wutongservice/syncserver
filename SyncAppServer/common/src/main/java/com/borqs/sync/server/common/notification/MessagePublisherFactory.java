package com.borqs.sync.server.common.notification;

/**
 * Date: 9/8/11
 * Time: 6:53 PM
 */
public interface MessagePublisherFactory {

    public static final String PTP_IDENTIFIER_SEND_CHANGED_CONTACTS_TO_ACCOUNT = "jms.ptp.producer.identifier.send.changed.contacts.to.account";
    public static final String PTP_CHANGED_CONTACTS_ACTION_CHANGE_VALUE = "change";
    public static final String PTP_CHANGED_CONTACTS_DATA_KEY = "data";
    public static final String PTP_CHANGED_CONTACTS_SENDER_ID_KEY = "uid";
	public static final String PTP_CHANGED_CONTACTS_IDS_KEY = "ids";
	public static final String PTP_CHANGED_CONTACTS_ACTION_KEY = "action";
    public static final String PTP_CHANGED_CONTACTS_ACTION_UPDATE_VALUE = "update";

    public MessagePublisher getPTPProducer(String identifier);
}
