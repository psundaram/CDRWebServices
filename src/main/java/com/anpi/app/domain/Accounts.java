package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Accounts {

	@JsonProperty("account_id")
	private String[] accountId;

	public String[] getAccountId() {
		return accountId;
	}

	@XmlElement(name="AccountId")
	public void setAccountId(String[] accountId) {
		this.accountId = accountId;
	}
	
	
}
