package com.anpi.app.domain;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@XmlRootElement(name = "root")
public class DirectionType {

	@JsonProperty("incoming_count")
	private int incomingCount;
	
	@JsonProperty("outgoing_count")
	private int outgoingCount;
	
	@JsonProperty("intercom_count")
	private int intercomCount;

	public int getIncomingCount() {
		return incomingCount;
	}

	@XmlElement(name="IncomingCount")
	public void setIncomingCount(int incomingCount) {
		this.incomingCount = incomingCount;
	}

	public int getOutgoingCount() {
		return outgoingCount;
	}

	@XmlElement(name="OutgoingCount")
	public void setOutgoingCount(int outgoingCount) {
		this.outgoingCount = outgoingCount;
	}

	public int getIntercomCount() {
		return intercomCount;
	}

	@XmlElement(name="IntercomCount")
	public void setIntercomCount(int intercomCount) {
		this.intercomCount = intercomCount;
	}

	
}
