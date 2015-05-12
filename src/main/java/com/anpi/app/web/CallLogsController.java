package com.anpi.app.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.axis.client.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import scala.runtime.StringAdd;

import com.anpi.app.domain.Accounts;
import com.anpi.app.domain.CallLogs;
import com.anpi.app.domain.EnhancedCallLogsEntry;
import com.anpi.app.domain.EnhancedCallLogsEntryList;
import com.anpi.app.domain.PhoneNumber;
import com.anpi.app.domain.TrafficBean;
import com.anpi.app.service.CallLogsService;
import com.anpi.app.util.CDRDBOperations;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@Controller
public class CallLogsController {
//	@Autowired
	 CallLogsService callLogsService = new CallLogsService();
	
	
	
	 @RequestMapping(value="/callLogsEndUser",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public String getCallLogsEndUser(@RequestBody String content) throws Exception{
	        System.out.println("getCallLogsNewTimeZone Enters: " + content);
	        String responseString = "<EnhancedCallLogs></EnhancedCallLogs>";
	        ObjectMapper mapper = new ObjectMapper();
			CallLogs callLogs = mapper.readValue(content, CallLogs.class);
	        EnhancedCallLogsEntryList listObj = callLogsService.getCallLogsForEndUser(callLogs);
	        responseString = new Gson().toJson(listObj,EnhancedCallLogsEntryList.class);
			System.out.println("responseString:" + responseString);
	        return responseString;

	    }
	 

	 @RequestMapping(value="/callLogsEndUser/xml",method=RequestMethod.POST,consumes=MediaType.APPLICATION_XML_VALUE,produces=MediaType.APPLICATION_XML_VALUE)
	    @ResponseBody
	    public String getXmlCallLogsEndUser(@RequestBody String content) throws Exception {
	        System.out.println("getCallLogsNewTimeZone Enters: " + content);
	        String responseString = "<EnhancedCallLogs></EnhancedCallLogs>";
	    	JAXBContext jaxbContext = JAXBContext.newInstance(CallLogs.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(content.toString());
			CallLogs callLogs = (CallLogs) unmarshaller.unmarshal(reader);
			System.out.println(callLogs.getEnterpriseId()+ ", "+callLogs.getAccounts() +" ," +callLogs.getPhoneNumbers().getPhoneNumber().get(0).getNumber());
			EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForEndUser(callLogs);
			responseString =  callLogsService.marshal(enhancedCallLogsEntryList);
			System.out.println(responseString);
			return responseString;
			
	 }
	    /**
	     * To get admin call logs details
	     * @throws IOException 
	     * @throws JsonMappingException 
	     * @throws JsonParseException 
	     */
	@RequestMapping(value = "/callLogsAdmin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getCallLogsAdmin(@RequestBody String content) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("callLogsAdmin");
		String responseString = "";
		ObjectMapper mapper = new ObjectMapper();
		CallLogs callLogs = mapper.readValue(content, CallLogs.class);
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdmin(callLogs);
		responseString = new Gson().toJson(enhancedCallLogsEntryList, EnhancedCallLogsEntryList.class);
		System.out.println("responseString:" + responseString);
		return responseString;

	}
	
	@RequestMapping(value = "/callLogsAdmin/xml", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String getXmlCallLogsAdmin(@RequestBody String content) throws Exception  {
		System.out.println("callLogsAdmin");
		String responseString = "";
		JAXBContext jaxbContext = JAXBContext.newInstance(CallLogs.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(content.toString());
		CallLogs callLogs = (CallLogs) unmarshaller.unmarshal(reader);
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdmin(callLogs);
		responseString = callLogsService.marshal(enhancedCallLogsEntryList);
		System.out.println("responseString:" + responseString);
		return responseString;

	}
	
	

}
