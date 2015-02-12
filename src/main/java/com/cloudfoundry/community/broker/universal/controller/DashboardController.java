package com.cloudfoundry.community.broker.universal.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.model.ServiceInstanceBinding;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceBindingServiceFactory;

@Controller
public class DashboardController extends BaseController{
	
	private static final String BASE_PATH = IdentifierConstants.DASHBOARD_BASE_PATH + "/{instanceId}/{bindingId}";
	
	private static final String BINDING_NOT_FOUND_TEMPLATE = "<html><body>Binding not found.</body></html>"; 
	
	public void InitializeController() throws Exception
	{
		serviceInstanceBindingService = ServiceInstanceBindingServiceFactory.getInstance(serviceType);
	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String dynamicHtml(@PathVariable String instanceId, @PathVariable String bindingId) {
	    try {
			ServiceInstanceBinding binding = serviceInstanceBindingService.getServiceInstanceBinding(instanceId, bindingId);
		} catch (Exception e) {
			return BINDING_NOT_FOUND_TEMPLATE;
		}
	    
	    return null;
	    
	}
}
