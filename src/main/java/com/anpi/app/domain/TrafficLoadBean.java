package com.anpi.app.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TrafficLoad")
public class TrafficLoadBean {
	private String responseCode;
	private String responseText;
	private String inBoundLoad;
	private String outBoundLoad;
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

}
