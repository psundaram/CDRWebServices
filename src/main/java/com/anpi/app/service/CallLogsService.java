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

import org.springframework.stereotype.Component;

import com.anpi.app.domain.CallLogs;
import com.anpi.app.domain.EnhancedCallLogsEntry;
import com.anpi.app.domain.EnhancedCallLogsEntryList;
import com.anpi.app.domain.PhoneNumber;
import com.anpi.app.util.CDRDBOperations;
import com.google.common.base.Strings;

@Component
public class CallLogsService {

	public EnhancedCallLogsEntryList getCallLogsForEndUser(CallLogs callLogs) {
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
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, dtFromDate.getTime(), cal.getTimeInMillis(), phoneNumberList, timeZoneMap);
			} else if (dtFromDate != null) {
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, dtFromDate.getTime(), new Date().getTime(), phoneNumberList, timeZoneMap);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, 0, cal.getTimeInMillis(), phoneNumberList, timeZoneMap);
			} else {
				callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, phoneNumberList, timeZoneMap);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception");
		}
		if (callLogsEntrys == null) {
			callLogsEntrys = dbOperations.getCallLogsTimeZoneNew(stEnterpriseId, accountIdList, phoneNumberList, timeZoneMap);
		}
		enhancedCallLogsEntryList.setEntryList(callLogsEntrys);
		return enhancedCallLogsEntryList;
	}

	public EnhancedCallLogsEntryList getCallLogsForAdmin(CallLogs callLogs) {
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
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), cal.getTimeInMillis(), timeZone);
			} else if (dtFromDate != null) {
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, dtFromDate.getTime(), new Date().getTime(), timeZone);
			} else if (dtToDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtToDate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.SECOND, -1);
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, 0, cal.getTimeInMillis(), timeZone);
			} else {
				callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, timeZone);
			}

		} catch (ParseException ex) {
			System.out.println("Parse Exception" + ex);
			ex.printStackTrace();
		}
		if (callLogsEntrys == null) {
			callLogsEntrys = dbOperations.getCallLogsAdmin(enterpriseId, timeZone);
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

}
