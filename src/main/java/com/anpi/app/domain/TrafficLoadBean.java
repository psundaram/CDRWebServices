package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "TrafficLoad")
public class TrafficLoadBean {
	@SerializedName("response_code")
	private String responseCode;
	@SerializedName("response_text")
	private String responseText;
	@SerializedName(value="in_bound")
	private String inBoundLoad;
	@SerializedName(value="out_bound")
	private String outBoundLoad;
	@SerializedName(value="inter_com")
	private String interComLoad;

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

	public String getInBoundLoad() {
		return inBoundLoad;
	}

	@XmlElement(name = "InBound")
	public void setInBoundLoad(String inBoundLoad) {
		this.inBoundLoad = inBoundLoad;
	}

	public String getOutBoundLoad() {
		return outBoundLoad;
	}

	@XmlElement(name = "OutBound")
	public void setOutBoundLoad(String outBoundLoad) {
		this.outBoundLoad = outBoundLoad;
	}

	public String getInterComLoad() {
		return interComLoad;
	}

	@XmlElement(name = "InterCom")
	public void setInterComLoad(String interComLoad) {
		this.interComLoad = interComLoad;
	}

	@Override
	public String toString() {
		return "TrafficLoadBean [responseCode=" + responseCode + ", responseText=" + responseText + ", inBoundLoad=" + inBoundLoad + ", outBoundLoad=" + outBoundLoad + ", interComLoad="
				+ interComLoad + "]";
	}
	
	
	
}
