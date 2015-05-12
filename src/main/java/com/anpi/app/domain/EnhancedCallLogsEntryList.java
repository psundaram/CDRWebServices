/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anpi.app.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "EnhancedCallLogs")
public class EnhancedCallLogsEntryList {

	@SerializedName("response_code")
	private String responseCode;

	@SerializedName("response_text")
	private String responseText;

	@SerializedName("enhanced_call_logs_extended_entry")
	private List<EnhancedCallLogsEntry> entryList;

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

}
