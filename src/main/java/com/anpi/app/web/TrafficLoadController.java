package com.anpi.app.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anpi.app.service.TrafficLoadService;

@Controller
@RequestMapping("/trafficLoad")
public class TrafficLoadController {


	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String getTrafficLoad(@RequestBody String content) throws Exception {
		return new TrafficLoadService().getTrafficLoad(content);
	}
}
