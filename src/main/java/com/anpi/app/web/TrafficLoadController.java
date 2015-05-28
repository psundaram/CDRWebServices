package com.anpi.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anpi.app.domain.TrafficBean;
import com.anpi.app.domain.TrafficLoadBean;
import com.anpi.app.service.TrafficLoadService;
import com.google.gson.Gson;

@Controller
@RequestMapping("/trafficLoad")
public class TrafficLoadController {

	@Autowired
	TrafficLoadService trafficLoadService;
	

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public TrafficLoadBean getTrafficLoad(@RequestBody TrafficBean trafficModel) throws Exception {
		System.out.println("trafficModel-->"+trafficModel);
//		JAXBContext jaxbContext = JAXBContext.newInstance(TrafficBean.class);
//		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//		StringReader reader = new StringReader(content.toString());
//		TrafficBean trafficModel = (TrafficBean) unmarshaller.unmarshal(reader);
		TrafficLoadBean trafficLoadBean = trafficLoadService.getTrafficLoad(trafficModel);
//		return trafficLoadService.marshal(trafficLoadBean);
		
		return trafficLoadBean;
	}
	
	
	@RequestMapping(value="/json",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public TrafficLoadBean getTrafficLoadJson(@RequestBody TrafficBean trafficBean) throws Exception {
		System.out.println("loadBean :" + trafficBean);
//		ObjectMapper mapper = new ObjectMapper();
//		TrafficBean trafficBean = mapper.readValue(loadBean, TrafficBean.class);
		
		TrafficLoadBean trafficLoadBean = trafficLoadService.getTrafficLoad(trafficBean);
		String responseString = new Gson().toJson(trafficLoadBean, TrafficLoadBean.class);
		System.out.println(responseString);
		return trafficLoadBean;
	}
	
	@RequestMapping(value="/intervals",method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public TrafficLoadBean getTrafficLoadNew(@RequestBody TrafficBean trafficModel) throws Exception {
		System.out.println("trafficModel-->"+trafficModel);
//		JAXBContext jaxbContext = JAXBContext.newInstance(TrafficBean.class);
//		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//		StringReader reader = new StringReader(content.toString());
//		TrafficBean trafficModel = (TrafficBean) unmarshaller.unmarshal(reader);
		TrafficLoadBean trafficLoadBean = trafficLoadService.getTrafficLoadNew(trafficModel);
//		return trafficLoadService.marshal(trafficLoadBean);
		System.out.println(trafficLoadBean);
		
		return trafficLoadBean;
	}
	
	
}
