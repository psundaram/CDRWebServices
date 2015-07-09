/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anpi.app.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "EnhancedCallLogs")
public class EnhancedCallLogsEntryList {

	@JsonProperty("response_code")
	private String responseCode;

	@JsonProperty("response_text")
	private String responseText;

	@JsonProperty("enhanced_call_logs_extended_entry")
	private List<EnhancedCallLogsEntry> entryList;
	
	@JsonProperty("direction_type_count")
	private DirectionType directionTypes;

	public String getResponseCode() {
		return responseCode;
	}

	@XmlElement(name = "ResponseCode")
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseText() {
		return responseText;
	}

	@XmlElement(name = "ResponseText")
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public List<EnhancedCallLogsEntry> getEntryList() {
		return entryList;
	}

	@XmlElement(name = "enhancedCallLogsExtendedEntry")
	public void setEntryList(List<EnhancedCallLogsEntry> entryList) {
		this.entryList = entryList;
	}

	@XmlElement(name = "DirectionTypeCount")
	public void setDirectionTypes(DirectionType directionTypes) {
		this.directionTypes = directionTypes;
	}

	public DirectionType getDirectionTypes() {
		return directionTypes;
	}

	
	
	

}
