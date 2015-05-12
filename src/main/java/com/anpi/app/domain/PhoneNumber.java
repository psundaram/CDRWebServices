package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneNumber {

	private String number;
	
	@JsonProperty("time_zone")
	private String timeZone;

	public String getNumber() {
		return number;
	}

	@XmlElement(name="Number")
	public void setNumber(String number) {
		this.number = number;
	}

	public String getTimeZone() {
		return timeZone;
	}

	@XmlElement(name="TimeZone")
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	

}
