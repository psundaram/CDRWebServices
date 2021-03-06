package com.anpi.app.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name="root")
public class TrafficBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("enterprise_id")
	private String enterpriseId;
	@JsonProperty("from_time")
	private String fromTime;
	@JsonProperty("to_time")
	private String toTime;
	@JsonProperty("inc_type")
	private String incType;
	@JsonProperty("inc_value")
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
	@Override
	public String toString() {
		return "TrafficBean [enterpriseId=" + enterpriseId + ", fromTime=" + fromTime + ", toTime=" + toTime + ", incType=" + incType
				+ ", incValue=" + incValue + "]";
	}

	
}
