package com.cloudfoundry.community.broker.universal.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.service.*;

@Controller
public class DashboardController extends BaseController{
	
	private static final String BASE_PATH = IdentifierConstants.DASHBOARD_BASE_PATH + "/{instanceId}"; 
	
	public void InitializeController() throws Exception
	{
		dashboardService = DashboardServiceFactory.getInstance(serviceType);
	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String dynamicHtml(@PathVariable String instanceId) {
	    return dashboardService.getDashboard(instanceId);
	}
}
