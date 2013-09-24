package com.borqs.sync.server.common.providers;

public class Photo {

    public static final Short EMPTY_PHOTO = 0;
    public static final Short PHOTO_IMAGE = 1;
    public static final Short PHOTO_URL   = 2;

	long contact;
	String type = String.valueOf(EMPTY_PHOTO);
	byte[] image;
	String url;
	
	public Photo(long contact, String type, byte[] image, String url) {
		this.contact = contact;
		this.type = type;
		this.image = image;
		this.url = url;
	}

	public long getContact() {
		return contact;
	}

	public void setContact(long contact) {
		this.contact = contact;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
