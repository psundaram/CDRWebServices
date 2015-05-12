package com.anpi.app.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneNumbers {
	
	@JsonProperty("phone_number")
	private List<PhoneNumber> phoneNumber;

	public List<PhoneNumber> getPhoneNumber() {
		return phoneNumber;
	}

	@XmlElement(name="PhoneNumber")
	public void setPhoneNumber(List<PhoneNumber> phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	

}
