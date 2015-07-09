package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("search_key")
	private String searchKey;
	@JsonProperty("from_time")
	private String fromTime;
	@JsonProperty("to_time")
	private String toTime;
	
	@JsonProperty("call_type")
	private String callType;
	
	@JsonProperty("page_number")
	private int pageNumber;
	
	@JsonProperty("page_size")
	private int pageSize;
	
	private Accounts accounts;
	
	@XmlElement(name="FromTime")
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	@XmlElement(name="ToTime")
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

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
	
	
	public String getCallType() {
		return callType;
	}

	@XmlElement(name="CallType")
	public void setCallType(String callType) {
		this.callType = callType;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	@XmlElement(name="PageNumber")
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	@XmlElement(name="PageSize")
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearchKey() {
		return searchKey;
	}
	@XmlElement(name="SearchKey")
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	@Override
	public String toString() {
		return "CallLogs [enterpriseId=" + enterpriseId + ", fromDate=" + fromDate + ", toDate=" + toDate + ", adminTimeZone=" + adminTimeZone + ", phoneNumbers=" + phoneNumbers + ", searchKey="
				+ searchKey + ", callType=" + callType + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", accounts=" + accounts + "]";
	}


	
}
