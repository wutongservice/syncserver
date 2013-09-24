package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.datasync.engine.IData;

public class SyncContactData implements IData {
	Contact mContact;
	
	public SyncContactData(Contact contact) {
		mContact = contact;
	}
	
	public Contact getContact() {
		return mContact;
	}
}
