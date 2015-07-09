package com.anpi.app.service;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anpi.app.dao.CallLogDAO;
import com.anpi.app.domain.CallLogs;
import com.anpi.app.domain.DirectionType;
import com.anpi.app.domain.EnhancedCallLogsEntry;
import com.anpi.app.domain.EnhancedCallLogsEntryList;
import com.anpi.app.domain.PhoneNumber;
import com.anpi.app.model.CallLogDetail;
import com.anpi.app.util.CDRDBOperations;
import com.anpi.app.util.CommonUtils;
import com.google.common.base.Strings;

@Service
@Transactional
public class CallLogsService {
	
	@Autowired
	CallLogDAO callLogDAO;

	public EnhancedCallLogsEntryList getCallLogsForEndUser(CallLogs callLogs) throws Exception {
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		String stEnterpriseId = null;
		if (Strings.isNullOrEmpty(callLogs.getEnterpriseId())) {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("EnterpriseId is mandatory");
			return enhancedCallLogsEntryList;
		} else
			stEnterpriseId = callLogs.getEnterpriseId();

		ArrayList<String> phoneNumberList = new ArrayList<String>();
		HashMap<String, String> timeZoneMap = new HashMap<String, String>();
		String accountIdList = "";
		List<PhoneNumber> listOfPhoneNumbers = callLogs.getPhoneNumbers().getPhoneNumber();
		for (int i = 0; i < listOfPhoneNumbers.size(); i++) {
			PhoneNumber phoneNumber = (PhoneNumber) listOfPhoneNumbers.get(i);
			String stPhoneNumber = null;
			String stTimeZone = null;
			stPhoneNumber = phoneNumber.getNumber();
			phoneNumberList.add(stPhoneNumber);

			stTimeZone = phoneNumber.getTimeZone();

			if (stTimeZone != null) {
				timeZoneMap.put(stPhoneNumber, stTimeZone);
			}
		}
		System.out.println("getAccountId:" + callLogs.getAccounts().getAccountId().length);
		String[] accountList = callLogs.getAccounts().getAccountId();
		System.out.println(accountList[0]);
		if (accountList.length == 0) {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("AccountId is mandatory");
			return enhancedCallLogsEntryList;
		}
		for (int i = 0; i < accountList.length; i++) {
			accountIdList = accountIdList + "\'" + accountList[i] + "\',";
		}
		if (accountIdList.length() != 0) {
			accountIdList = accountIdList.substring(0, accountIdList.length() - 1);
		}
		System.out.println(accountIdList);
		CDRDBOperations dbOperations = new CDRDBOperations();
		ArrayList<EnhancedCallLogsEntry> callLogsEntrys = null;
		String stFromDate = null;
		String stToDate = null;
		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dtFromDate = null;
		Date dtToDate = null;

		try {
			if (!Strings.isNullOrEmpty(callLogs.getFromDate())) {
				stFromDate = callLogs.getFromDate();
				dtFromDate = sdf.parse(stFromDate);
			} else
				System.out.println("No From Date");
			if (!Strings.isNullOrEmpty(callLogs.getToDate())) {
				stToDate = callLogs.getToDate();
				dtToDate = sdf.parse(stToDate);
			} else
				System.out.println("No To Date");
			if (dtFromDate != null && dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, dtFromDate.getTime(), cal.getTimeInMillis(), phoneNumberList, timeZoneMap,callLogs);
			} else if (dtFromDate != null) {
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, dtFromDate.getTime(), new Date().getTime(), phoneNumberList, timeZoneMap,callLogs);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, 0, cal.getTimeInMillis(), phoneNumberList, timeZoneMap,callLogs);
			} else {
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, phoneNumberList, timeZoneMap,callLogs);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception");
		}
		if (callLogsEntrys == null) {
			callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, phoneNumberList, timeZoneMap,callLogs);
		}
		enhancedCallLogsEntryList.setEntryList(callLogsEntrys);
		return enhancedCallLogsEntryList;
	}

	public EnhancedCallLogsEntryList getCallLogsForAdmin(CallLogs callLogs) throws Exception {
		String enterpriseId = null;
		String timeZone = null;
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		if (!Strings.isNullOrEmpty(callLogs.getEnterpriseId())) {
			enterpriseId = callLogs.getEnterpriseId();
		} else {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("EnterpriseId is mandatory");
			return enhancedCallLogsEntryList;
		}

		if (!Strings.isNullOrEmpty(callLogs.getAdminTimeZone())) {
			timeZone = callLogs.getAdminTimeZone();
		} else {
			System.out.println("TimeZone not found in the input xml");
		}
		CDRDBOperations dbOperations = new CDRDBOperations();
		ArrayList<EnhancedCallLogsEntry> callLogsEntrys = null;
		String stFromDate = null;
		String stToDate = null;
		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dtFromDate = null;
		Date dtToDate = null;

		try {
			if (!Strings.isNullOrEmpty(callLogs.getFromDate())) {
				stFromDate = callLogs.getFromDate();
				dtFromDate = sdf.parse(stFromDate);
			} else
				System.out.println("No From Date");
			if (!Strings.isNullOrEmpty(callLogs.getToDate())) {
				stToDate = callLogs.getToDate();
				dtToDate = sdf.parse(stToDate);
			} else
				System.out.println("No To Date");
			if (dtFromDate != null && dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				// System.out.println(dtFromDate);//.getTime());
				// System.out.println(cal.getTime());//.getTimeInMillis());
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), cal.getTimeInMillis(), timeZone,callLogs);
			} else if (dtFromDate != null) {
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), new Date().getTime(), timeZone,callLogs);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, 0, cal.getTimeInMillis(), timeZone,callLogs);
			} else {
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, timeZone,callLogs);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception" + ex);
			ex.printStackTrace();
		}
		if (callLogsEntrys == null) {
			callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, timeZone,callLogs);
		}
		enhancedCallLogsEntryList.setEntryList(callLogsEntrys);
		return enhancedCallLogsEntryList;

	}

	public String marshal(EnhancedCallLogsEntryList enhancedCallLogsEntryList) throws Exception {
		String responseString = "<EnhancedCallLogs></EnhancedCallLogs>";
		if (enhancedCallLogsEntryList != null) {
			JAXBContext jaxb = JAXBContext.newInstance(EnhancedCallLogsEntryList.class);
			Marshaller jaxbMarshaller = jaxb.createMarshaller();
			StringWriter sw = new StringWriter();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(enhancedCallLogsEntryList, sw);
			responseString = sw.toString();
		}
		return responseString;
	}
	
	
	// Adding Filter and Pagination
	
	public EnhancedCallLogsEntryList getCallLogsForAdminNew(CallLogs callLogs) throws Exception {
		String enterpriseId = null;
		String timeZone = null;
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		if (!Strings.isNullOrEmpty(callLogs.getEnterpriseId())) {
			enterpriseId = callLogs.getEnterpriseId();
		} else {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("EnterpriseId is mandatory");
			return enhancedCallLogsEntryList;
		}

		if (!Strings.isNullOrEmpty(callLogs.getAdminTimeZone())) {
			timeZone = callLogs.getAdminTimeZone();
		} else {
			System.out.println("TimeZone not found in the input xml");
		}
		ArrayList<EnhancedCallLogsEntry> callLogsEntrys = null;
		String stFromDate = callLogs.getFromTime();
		String stToDate = callLogs.getToTime();
		System.out.println("StFromDate :" + stFromDate + "stToDate :" +stToDate);
		Date dtFromDate = null;
		Date dtToDate = null;
		

		try {
			if (!Strings.isNullOrEmpty(stFromDate)) {
				dtFromDate = new Date(Long.parseLong(stFromDate));
			} else
				System.out.println("No From Date");
			if (!Strings.isNullOrEmpty(stToDate)) {
				dtToDate = new Date(Long.parseLong(stToDate));
			} else
				System.out.println("No To Date");
			if (dtFromDate != null && dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				enhancedCallLogsEntryList = getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), cal.getTimeInMillis(), timeZone,callLogs);
			} else if (dtFromDate != null) {
				enhancedCallLogsEntryList = getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), new Date().getTime(), timeZone,callLogs);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				enhancedCallLogsEntryList = getCallLogsAdmin(enterpriseId, 0l, cal.getTimeInMillis(), timeZone,callLogs);
			} else {
				enhancedCallLogsEntryList = getCallLogsAdmin(enterpriseId,null,null, timeZone,callLogs);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception" + ex);
			ex.printStackTrace();
		}
		if (enhancedCallLogsEntryList == null) {
			enhancedCallLogsEntryList = getCallLogsAdmin(enterpriseId,null,null,timeZone,callLogs);
		}
		
		return enhancedCallLogsEntryList;

	}

	public List<CallLogDetail> getCallLogs() {
		System.out.println("CallLogs enter");
		List<CallLogDetail> list = callLogDAO.search();
		System.out.println("call Logs exit");
		return list;
	}
	
	
	public EnhancedCallLogsEntryList getCallLogsAdmin(String enterpriseId,Long time, Long toTime,String timeZone,CallLogs callLogs) throws Exception {
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		EnhancedCallLogsEntry callLogsEntry = null;
		ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
		List<CallLogDetail> listOfCallLogDetails = callLogDAO.getCallLogsAdmin(enterpriseId, time, toTime,callLogs.getCallType(),callLogs.getSearchKey(),callLogs.getPageNumber(),callLogs.getPageSize());
		for (CallLogDetail callLogDetail : listOfCallLogDetails) {
				callLogsEntry = new EnhancedCallLogsEntry();
				callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(callLogDetail.getFromNumber()));
				callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(callLogDetail.getToNumber()));
				callLogsEntry.setDatecreated(callLogDetail.getStartTime().longValue());
				callLogsEntry.setAnswerIndicator(callLogDetail.getAnswerIndicator());
				callLogsEntry.setCallDate(CommonUtils.getDate(callLogDetail.getStartTime().longValue()));
				callLogsEntry.setStarttime(callLogDetail.getStartTime().longValue());
				callLogsEntry.setEndtime(callLogDetail.getEndTime().longValue());
				callLogsEntry.setDurationS(CommonUtils.getDuration(callLogDetail.getDuration()));
				callLogsEntry.setDuration(callLogDetail.getDuration());
				callLogsEntry.setDirection(callLogDetail.getDirectionType());
				callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(callLogDetail.getStartTime().longValue(), timeZone));
				list.add(callLogsEntry);
		}
		DirectionType directionType = callLogDAO.getCallLogCountForAdmin(enterpriseId, time, toTime);
		enhancedCallLogsEntryList.setEntryList(list);
		enhancedCallLogsEntryList.setDirectionTypes(directionType);
		return enhancedCallLogsEntryList;
	}
	
	

	public EnhancedCallLogsEntryList getCallLogsForEndUserNew(CallLogs callLogs) throws Exception {
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		String stEnterpriseId = null;
		if (Strings.isNullOrEmpty(callLogs.getEnterpriseId())) {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("EnterpriseId is mandatory");
			return enhancedCallLogsEntryList;
		} else
			stEnterpriseId = callLogs.getEnterpriseId();

		ArrayList<String> phoneNumberList = new ArrayList<String>();
		HashMap<String, String> timeZoneMap = new HashMap<String, String>();
		String accountIdList = "";
		List<PhoneNumber> listOfPhoneNumbers = callLogs.getPhoneNumbers().getPhoneNumber();
		for (int i = 0; i < listOfPhoneNumbers.size(); i++) {
			PhoneNumber phoneNumber = (PhoneNumber) listOfPhoneNumbers.get(i);
			String stPhoneNumber = null;
			String stTimeZone = null;
			stPhoneNumber = phoneNumber.getNumber();
			phoneNumberList.add(stPhoneNumber);

			stTimeZone = phoneNumber.getTimeZone();

			if (stTimeZone != null) {
				timeZoneMap.put(stPhoneNumber, stTimeZone);
			}
		}
		System.out.println("getAccountId:" + callLogs.getAccounts().getAccountId().length);
		String[] accountList = callLogs.getAccounts().getAccountId();
		System.out.println(accountList[0]);
		if (accountList.length == 0) {
			enhancedCallLogsEntryList.setResponseCode("2");
			enhancedCallLogsEntryList.setResponseText("AccountId is mandatory");
			return enhancedCallLogsEntryList;
		}
		for (int i = 0; i < accountList.length; i++) {
			accountIdList = accountIdList + "\'" + accountList[i] + "\',";
		}
		if (accountIdList.length() != 0) {
			accountIdList = accountIdList.substring(0, accountIdList.length() - 1);
		}
		System.out.println(accountIdList);
		CDRDBOperations dbOperations = new CDRDBOperations();
		ArrayList<EnhancedCallLogsEntry> callLogsEntrys = null;
		String stFromDate = callLogs.getFromTime();
		String stToDate = callLogs.getToTime();
		Date dtFromDate = null;
		Date dtToDate = null;

		try {
			if (!Strings.isNullOrEmpty(stFromDate)) {
				dtFromDate = new Date(Long.parseLong(stFromDate));
			} else
				System.out.println("No From Date");
			if (!Strings.isNullOrEmpty(stToDate)) {
				dtToDate = new Date(Long.parseLong(stToDate));
			} else
				System.out.println("No To Date");
			if (dtFromDate != null && dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				enhancedCallLogsEntryList = getCallLogsTimeZone(stEnterpriseId, accountIdList, dtFromDate.getTime(), cal.getTimeInMillis(), phoneNumberList, timeZoneMap,callLogs);
			} else if (dtFromDate != null) {
				enhancedCallLogsEntryList = getCallLogsTimeZone(stEnterpriseId, accountIdList, dtFromDate.getTime(), new Date().getTime(), phoneNumberList, timeZoneMap,callLogs);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				enhancedCallLogsEntryList = getCallLogsTimeZone(stEnterpriseId, accountIdList, 0l, cal.getTimeInMillis(), phoneNumberList, timeZoneMap,callLogs);
			} else {
				enhancedCallLogsEntryList = getCallLogsTimeZone(stEnterpriseId, accountIdList,null,null, phoneNumberList, timeZoneMap,callLogs);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception");
		}
		if (enhancedCallLogsEntryList == null) {
			enhancedCallLogsEntryList = getCallLogsTimeZone(stEnterpriseId, accountIdList,null,null, phoneNumberList, timeZoneMap,callLogs);
		}
//		enhancedCallLogsEntryList.setEntryList(callLogsEntrys);
		return enhancedCallLogsEntryList;
	}
	
	
	public EnhancedCallLogsEntryList getCallLogsTimeZone(String enterpriseId, String accountIdList, Long time, Long toTime, ArrayList<String> phoneNumberList,HashMap<String, String> timeZoneMap,CallLogs callLogs) throws Exception {
		EnhancedCallLogsEntryList enhancedCallLogsEntryList = new EnhancedCallLogsEntryList();
		EnhancedCallLogsEntry callLogsEntry = null;
	     ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	     List<CallLogDetail> listOfCallLogDetails = callLogDAO.getCallLogsTimeZoneNew(enterpriseId, accountIdList, time, toTime,callLogs.getCallType(),callLogs.getSearchKey(),callLogs.getPageNumber(),callLogs.getPageSize());
	     for(CallLogDetail callLogDetail : listOfCallLogDetails){
	          callLogsEntry = new EnhancedCallLogsEntry();
	          callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(callLogDetail.getFromNumber()));
	          callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(callLogDetail.getToNumber()));
	          callLogsEntry.setDatecreated(callLogDetail.getStartTime().longValue());
	          callLogsEntry.setAnswerIndicator(callLogDetail.getAnswerIndicator());
	          callLogsEntry.setCallDate(CommonUtils.getDate(callLogDetail.getStartTime().longValue()));
	          callLogsEntry.setStarttime(callLogDetail.getStartTime().longValue());
	          callLogsEntry.setEndtime(callLogDetail.getEndTime().longValue());
	          callLogsEntry.setDuration(callLogDetail.getDuration());
	          callLogsEntry.setDurationS(CommonUtils.getDuration(callLogDetail.getDuration()));
	          callLogsEntry.setDirection(callLogDetail.getDirectionType());
	          if (timeZoneMap.get(callLogsEntry.getFromnumber())!=null) { //OutBound and Intercom
	              callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(callLogDetail.getStartTime().longValue(), timeZoneMap.get(callLogsEntry.getFromnumber())));
	          } else {
	              callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(callLogDetail.getStartTime().longValue(), timeZoneMap.get(callLogsEntry.getTonumber())));
	          }
	          list.add(callLogsEntry);
	  	   }
	      System.out.println(list.size());
	      DirectionType directionType = callLogDAO.getCallLogCount(enterpriseId, accountIdList, time, toTime);
	      enhancedCallLogsEntryList.setEntryList(list);
	      enhancedCallLogsEntryList.setDirectionTypes(directionType);
		return enhancedCallLogsEntryList;
	}
	
	
}
