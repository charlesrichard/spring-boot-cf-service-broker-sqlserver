package org.cloudfoundry.community.servicebroker.sqlserver.controller;

import org.cloudfoundry.community.servicebroker.controller.BaseController;
import org.cloudfoundry.community.servicebroker.sqlserver.constants.IdentifierConstants;
import org.cloudfoundry.community.servicebroker.sqlserver.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class DashboardController extends BaseController {
	
	private DashboardService dashboardService;
	public static final String BASE_PATH = IdentifierConstants.DASHBOARD_BASE_PATH;
	
	@Autowired
	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public ResponseEntity<String> getDashboard(@PathVariable String instanceId) {
	    return new ResponseEntity<String>(dashboardService.getDashboard(instanceId), HttpStatus.OK);
	}
}
