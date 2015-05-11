package com.anpi.app.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.anpi.app.domain.TrafficBean;
import com.anpi.app.domain.TrafficLoadBean;
import com.anpi.app.util.CDRDBOperations;
import com.google.common.base.Strings;

@Component
public class TrafficLoadService {

	public TrafficLoadBean getTrafficLoad(TrafficBean trafficModel) throws Exception {
		TrafficLoadBean loadBean = new TrafficLoadBean();
		String stEnterpriseId = trafficModel.getEnterpriseId();
		// Null or Empty Check - EnterpriseId
		if (Strings.isNullOrEmpty(stEnterpriseId)) {
			loadBean.setResponseCode("2");
			loadBean.setResponseText("EnterpriseId is mandatory");
			return loadBean;
		}
		CDRDBOperations dbOperations = new CDRDBOperations();
		String stFromDate = trafficModel.getFromTime();
		String stToDate = trafficModel.getToTime();
		String incremenType = trafficModel.getIncType();
		Date dtFromDate = null;
		Date dtToDate = null;

		long inIcrementType = 0;
		long inIncrementValue = 0;
		// Null or Empty Check - IncType
		if (Strings.isNullOrEmpty(incremenType)) {
			loadBean.setResponseCode("2");
			loadBean.setResponseText("IncType is mandatory");
			return loadBean;
		} else {
			inIcrementType = getIncrementTypeValue(incremenType.trim());
			if (inIcrementType == 0) {
				loadBean.setResponseCode("2");
				loadBean.setResponseText("Invalid IncType");
				return loadBean;
			}
		}
		// Null or Empty Check - Incvalue
		if (Strings.isNullOrEmpty(trafficModel.getIncValue())) {
			loadBean.setResponseCode("2");
			loadBean.setResponseText("IncValue is mandatory");
			return loadBean;
		} else {
			inIncrementValue = Long.parseLong(trafficModel.getIncValue());
			if (inIncrementValue <= 0) {
				loadBean.setResponseCode("2");
				loadBean.setResponseText("Invalid IncValue");
				return loadBean;
			} else {
				inIncrementValue = inIncrementValue * inIcrementType;
			}
		}
		try {
			if (!Strings.isNullOrEmpty(stFromDate)) {
				dtFromDate = new Date(Long.parseLong(stFromDate));
			} else {
				System.out.println("No From Time");
			}
			if (!Strings.isNullOrEmpty(stToDate)) {
				dtToDate = new Date(Long.parseLong(stToDate));
			} else {
				System.out.println("No To Time");
			}
		} catch (Exception e) {
			loadBean.setResponseCode("2");
			loadBean.setResponseText("Invalid Date");
			return loadBean;
		}
		if (dtFromDate != null && dtToDate != null) {
			loadBean = dbOperations.getTrafficLoad(stEnterpriseId, dtFromDate.getTime(), dtToDate.getTime(), inIncrementValue);
		} else if (dtFromDate != null) {
			// Set current time for todate
			loadBean = dbOperations.getTrafficLoad(stEnterpriseId, dtFromDate.getTime(), new Date().getTime(), inIncrementValue);
		} else if (dtToDate != null) {
			// Set from date to 0
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtToDate);
			cal.add(Calendar.DATE, 1);
			cal.add(Calendar.SECOND, -1);
			loadBean = dbOperations.getTrafficLoad(stEnterpriseId, 0, cal.getTimeInMillis(), inIncrementValue);
		} else {
			loadBean.setResponseCode("2");
			loadBean.setResponseText("Request should contain either FromTime or ToTime");
			return loadBean;
		}
		return loadBean;
	}

	public String marshal(TrafficLoadBean loadBean) throws Exception {
		String responseString = "<TrafficLoad></TrafficLoad>";
		if (loadBean != null) {
			JAXBContext jaxb = JAXBContext.newInstance(TrafficLoadBean.class);
			Marshaller jaxbMarshaller = jaxb.createMarshaller();
			StringWriter sw = new StringWriter();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(loadBean, sw);
			responseString = sw.toString();
		}
		return responseString;
	}

	public int getIncrementTypeValue(String stIncType) {
		if (stIncType.equals("SEC"))
			return 1000;
		else if (stIncType.equals("MIN"))
			return 1000 * 60;
		else if (stIncType.equals("HOUR"))
			return 1000 * 60 * 60;
		else if (stIncType.equals("DAY"))
			return 1000 * 60 * 60 * 24;
		else
			return 0;
	}
}
