package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name="root")
public class CallLogs {

	@JsonProperty("enterprise_id")
	private String enterpriseId;
	@JsonProperty("from_date")
	private String fromDate;
	@JsonProperty("to_date")
	private String toDate;
	@JsonProperty("admin_time_zone")
	private String adminTimeZone;
	@JsonProperty("phone_numbers")
	private PhoneNumbers phoneNumbers;
	
	private Accounts accounts;

	@XmlElement(name="EnterpriseId")
	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	@XmlElement(name="FromDate")
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	@XmlElement(name="ToDate")
	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@XmlElement(name="AdminTimeZone")
	public String getAdminTimeZone() {
		return adminTimeZone;
	}

	public void setAdminTimeZone(String adminTimeZone) {
		this.adminTimeZone = adminTimeZone;
	}

	@XmlElement(name="PhoneNumbers")
	public PhoneNumbers getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(PhoneNumbers phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	@XmlElement(name="Accounts")
	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}

	
}
