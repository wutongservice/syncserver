package com.borqs.sync.server.common.account;

public class UserInfo {
	private String userId;

	private String login_email1;
	private String login_email2;
	private String login_email3;

	private String login_phone1;
	private String login_phone2;
	private String login_phone3;

	public UserInfo() {
		super();
	}

	public UserInfo(String userId, String login_email1, String login_email2,
			String login_email3, String login_phone1, String login_phone2,
			String login_phone3) {
		super();
		this.userId = userId;
		this.login_email1 = login_email1;
		this.login_email2 = login_email2;
		this.login_email3 = login_email3;
		this.login_phone1 = login_phone1;
		this.login_phone2 = login_phone2;
		this.login_phone3 = login_phone3;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogin_email1() {
		return login_email1;
	}

	public void setLogin_email1(String login_email1) {
		this.login_email1 = login_email1;
	}

	public String getLogin_email2() {
		return login_email2;
	}

	public void setLogin_email2(String login_email2) {
		this.login_email2 = login_email2;
	}

	public String getLogin_email3() {
		return login_email3;
	}

	public void setLogin_email3(String login_email3) {
		this.login_email3 = login_email3;
	}

	public String getLogin_phone1() {
		return login_phone1;
	}

	public void setLogin_phone1(String login_phone1) {
		this.login_phone1 = login_phone1;
	}

	public String getLogin_phone2() {
		return login_phone2;
	}

	public void setLogin_phone2(String login_phone2) {
		this.login_phone2 = login_phone2;
	}

	public String getLogin_phone3() {
		return login_phone3;
	}

	public void setLogin_phone3(String login_phone3) {
		this.login_phone3 = login_phone3;
	}
}
