package com.borqs.sync.server.common.providers;

public class MappingContactItem {
	
	private String ownerid;
	private Long contactid;
	private String borqsid;

	private int type;
	private String value;

	public MappingContactItem(String ownerid, Long contactid, String borqsid,
			int type, String value) {
		super();
		this.ownerid = ownerid;
		this.contactid = contactid;
		this.borqsid = borqsid;
		this.type = type;
		this.value = value;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public Long getContactid() {
		return contactid;
	}

	public void setContactid(Long contactid) {
		this.contactid = contactid;
	}

	public String getBorqsid() {
		return borqsid;
	}

	public void setBorqsid(String borqsid) {
		this.borqsid = borqsid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
