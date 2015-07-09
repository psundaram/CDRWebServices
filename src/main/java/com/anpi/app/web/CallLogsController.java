package com.anpi.app.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anpi.app.domain.CallLogs;
import com.anpi.app.domain.EnhancedCallLogsEntryList;
import com.anpi.app.model.CallLogDetail;
import com.anpi.app.service.CallLogsService;

@Controller
public class CallLogsController {
	@Autowired
	 CallLogsService callLogsService;
	
	@RequestMapping(value="/getCall",method=RequestMethod.GET)
	@ResponseBody
	public List<CallLogDetail> getCallLogs(){
		System.out.println("getCallLogs");
		List<CallLogDetail> list = callLogsService.getCallLogs();
		System.out.println("list"+list.size());
		return list;
		
	}
	
	 @RequestMapping(value="/callLogsEndUser",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public EnhancedCallLogsEntryList getCallLogsEndUser(@RequestBody CallLogs callLogs) throws Exception{
	        System.out.println("getCallLogsNewTimeZone Enters: " + callLogs);
	        EnhancedCallLogsEntryList listObj = callLogsService.getCallLogsForEndUser(callLogs);
	        return listObj;

	    }
	 
	 @RequestMapping(value="/callLogsEndUserNew",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public EnhancedCallLogsEntryList getCallLogsEndUserNew(@RequestBody CallLogs callLogs) throws Exception{
	        System.out.println("getCallLogsNewTimeZone Enters: " + callLogs.toString());
	        EnhancedCallLogsEntryList listObj = callLogsService.getCallLogsForEndUserNew(callLogs);
	        return listObj;

	    }
	 
	 
	 @RequestMapping(value="/callLogsEndUserNew/xml",method=RequestMethod.POST,consumes=MediaType.APPLICATION_XML_VALUE,produces=MediaType.APPLICATION_XML_VALUE)
	    @ResponseBody
	    public EnhancedCallLogsEntryList getCallLogsEndUserNewXml(@RequestBody CallLogs callLogs) throws Exception{
	        System.out.println("getCallLogsNewTimeZone Enters: " + callLogs.toString());
	        EnhancedCallLogsEntryList listObj = callLogsService.getCallLogsForEndUserNew(callLogs);
	        return listObj;

	    }
	 

	 @RequestMapping(value="/callLogsEndUser/xml",method=RequestMethod.POST,consumes=MediaType.APPLICATION_XML_VALUE,produces=MediaType.APPLICATION_XML_VALUE)
	    @ResponseBody
	    public EnhancedCallLogsEntryList getXmlCallLogsEndUser(@RequestBody CallLogs callLogs) throws Exception {
	        System.out.println("getCallLogsNewTimeZone Enters: " + callLogs.toString());
			EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForEndUser(callLogs);
			return enhancedCallLogsEntryList;
			
	 }
	    /**
	     * To get admin call logs details
	     * @throws Exception 
	     */
	@RequestMapping(value = "/callLogsAdmin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public EnhancedCallLogsEntryList getCallLogsAdmin(@RequestBody CallLogs callLogs) throws Exception {
		System.out.println("callLogsAdmin");
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdmin(callLogs);
		return enhancedCallLogsEntryList;

	}
	
	@RequestMapping(value = "/callLogsAdminNew", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public EnhancedCallLogsEntryList getCallLogsAdminNew(@RequestBody CallLogs callLogs) throws Exception{
		System.out.println("callLogsAdmin New");
		System.out.println(callLogs.toString());
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdminNew(callLogs);
		return enhancedCallLogsEntryList;

	}
	
	@RequestMapping(value = "/callLogsAdminNew/xml", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public EnhancedCallLogsEntryList getCallLogsAdminNewXml(@RequestBody CallLogs callLogs) throws Exception {
		System.out.println("callLogsAdmin New");
		System.out.println(callLogs.toString());
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdminNew(callLogs);
		return enhancedCallLogsEntryList;

	}
	
	@RequestMapping(value = "/callLogsAdmin/xml", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public EnhancedCallLogsEntryList getXmlCallLogsAdmin(@RequestBody CallLogs callLogs) throws Exception  {
		System.out.println("callLogsAdmin");
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = callLogsService.getCallLogsForAdmin(callLogs);
		return enhancedCallLogsEntryList;

	}
	
	

}
