package com.borqs.sync.server.common.providers;

public class ContactMapping {
	private String ownerid;
	private Long contactid;
	private String borqsid;

	public ContactMapping() {
		super();
	}

	public ContactMapping(String ownerid, Long contactid, String borqsid) {
		super();
		this.ownerid = ownerid;
		this.contactid = contactid;
		this.borqsid = borqsid;
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

    public String toString(){
        return "borqsId:" + borqsid +", contactId:" +contactid;
    }
}
