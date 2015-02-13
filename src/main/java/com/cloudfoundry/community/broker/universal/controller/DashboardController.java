package com.cloudfoundry.community.broker.universal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.service.*;

@Controller
public class DashboardController extends BaseController{
	
	public static final String BASE_PATH = IdentifierConstants.DASHBOARD_BASE_PATH; 
	
	public void InitializeController() throws Exception
	{
		dashboardService = DashboardServiceFactory.getInstance(serviceType);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public ResponseEntity<String> getDashboard(@PathVariable String instanceId) {
	    return new ResponseEntity<String>(dashboardService.getDashboard(instanceId), HttpStatus.OK);
	}
}
