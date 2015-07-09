package com.anpi.app.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "tbCallLog")
public class CallLogDetail implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="SourceRecordID")
	private BigInteger sourceRecordID;
	
	@Id
	@Column(name="callid")
	private String callId;
	
	@Column(name = "enterpriseid")
	private String enterpriseId;

	@Column(name = "fromnumber")
	private String fromNumber;

	@Column(name = "tonumber")
	private String toNumber;

	@Column(name = "startTime")
	private Long startTime;
	@Column(name = "endtime")
	private Long endTime;
	@Column(name = "duration")
	private int duration;
	@Column(name = "datecreated")
	private BigInteger datecreated;
	@Column(name = "direction")
	private String direction;
	@Column(name = "subscriberid")
	private String subscriberId;
	@Column(name = "answerindicator")
	private String answerIndicator;
	
	
	@Transient
	private int directionType;
	
	@Transient
	private String accountFilter;
	
	

	public String getAccountFilter() {
		return accountFilter;
	}

	public void setAccountFilter(String accountFilter) {
		this.accountFilter = accountFilter;
	}

	public int getDirectionType() {
		return directionType;
	}

	public void setDirectionType(int directionType) {
		this.directionType = directionType;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}

	public String getToNumber() {
		return toNumber;
	}

	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public BigInteger getDatecreated() {
		return datecreated;
	}

	public void setDatecreated(BigInteger datecreated) {
		this.datecreated = datecreated;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getAnswerIndicator() {
		return answerIndicator;
	}

	public void setAnswerIndicator(String answerIndicator) {
		this.answerIndicator = answerIndicator;
	}

	public BigInteger getSourceRecordID() {
		return sourceRecordID;
	}

	public void setSourceRecordID(BigInteger sourceRecordID) {
		this.sourceRecordID = sourceRecordID;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

}
