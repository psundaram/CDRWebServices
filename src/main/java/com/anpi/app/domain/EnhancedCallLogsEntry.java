/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "enhancedCallLogsExtendedEntry")
public class EnhancedCallLogsEntry {

	@SerializedName("date")
	private String callDate;
	@SerializedName("milliseconds")
	private long datecreated;
	@SerializedName("from_name")
	private String fromName;
	@SerializedName("from_last_name")
	private String fromLastName;
	@SerializedName("from_number")
	private String fromnumber;
	@SerializedName("to_name")
	private String toName;
	@SerializedName("to_last_name")
	private String toLastName;
	@SerializedName("to_number")
	private String tonumber;
	@SerializedName("start_time")
	private long starttime;
	@SerializedName("end_time")
	private long endtime;
	private int status;
	@SerializedName("bound")
	private int direction;
	@SerializedName("from_extension")
	private String fromExtension;
	@SerializedName("to_extension")
	private String toExtension;
	private long duration;
	@SerializedName("time_zone")
	private String timeZone;
	@SerializedName("answer_indicator")
	private String answerIndicator;

	@XmlElement(name = "answerIndicator")
	public String getAnswerIndicator() {
		return answerIndicator;
	}

	public void setAnswerIndicator(String answerIndicator) {
		this.answerIndicator = answerIndicator;
	}

	public String getCallDate() {
		return callDate;
	}

	@XmlElement(name = "date")
	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}

	public long getDuration() {
		return duration;
	}

	@XmlElement(name = "duration")
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDatecreated() {
		return datecreated;
	}

	@XmlElement(name = "milliseconds")
	public void setDatecreated(long datecreated) {
		this.datecreated = datecreated;
	}

	public String getFromName() {
		return fromName;
	}

	@XmlElement(name = "fromName")
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromLastName() {
		return fromLastName;
	}

	@XmlElement(name = "fromLastName")
	public void setFromLastName(String fromLastName) {
		this.fromLastName = fromLastName;
	}

	@XmlElement(name = "fromNumber")
	public String getFromnumber() {
		return fromnumber;
	}

	public void setFromnumber(String fromnumber) {
		this.fromnumber = fromnumber;
	}

	public String getToName() {
		return toName;
	}

	@XmlElement(name = "toName")
	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getToLastName() {
		return toLastName;
	}

	@XmlElement(name = "toLastName")
	public void setToLastName(String toLastName) {
		this.toLastName = toLastName;
	}

	public String getTonumber() {
		return tonumber;
	}

	@XmlElement(name = "toNumber")
	public void setTonumber(String tonumber) {
		this.tonumber = tonumber;
	}

	public long getStarttime() {
		return starttime;
	}

	@XmlElement(name = "startTime")
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public long getEndtime() {
		return endtime;
	}

	@XmlElement(name = "endTime")
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public int getStatus() {
		return status;
	}

	@XmlElement(name = "status")
	public void setStatus(int status) {
		this.status = status;
	}

	public int getDirection() {
		return direction;
	}

	@XmlElement(name = "bound")
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getFromExtension() {
		return fromExtension;
	}

	@XmlElement(name = "fromExtension")
	public void setFromExtension(String fromExtension) {
		this.fromExtension = fromExtension;
	}

	public String getToExtension() {
		return toExtension;
	}

	@XmlElement(name = "toExtension")
	public void setToExtension(String toExtension) {
		this.toExtension = toExtension;
	}

	@XmlElement(name = "timeZone")
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

}
