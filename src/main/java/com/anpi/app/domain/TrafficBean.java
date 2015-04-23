package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class TrafficBean {
	
	private String enterpriseId;
	private String fromTime;
	private String toTime;
	private String incType;
	private String incValue;
	
	@XmlElement(name="EnterpriseId")
	public String getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
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
	@XmlElement(name="IncType")
	public String getIncType() {
		return incType;
	}
	public void setIncType(String incType) {
		this.incType = incType;
	}
	@XmlElement(name="IncValue")
	public String getIncValue() {
		return incValue;
	}
	public void setIncValue(String incValue) {
		this.incValue = incValue;
	}

}
