package com.cloudfoundry.community.broker.universal.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloudfoundry.community.broker.universal.controller.BaseController;
import com.cloudfoundry.community.broker.universal.controller.ServiceInstanceController;
import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceDefinitionDoesNotExistException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceExistsException;
import com.cloudfoundry.community.broker.universal.model.CreateServiceInstanceRequest;
import com.cloudfoundry.community.broker.universal.model.CreateServiceInstanceResponse;
import com.cloudfoundry.community.broker.universal.model.ErrorMessage;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceServiceFactory;

@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);
	
	public ServiceInstanceController() throws Exception
	{
		super();
		serviceInstanceService = ServiceInstanceServiceFactory.getInstance(serviceType);
	}
	
	// There is no getter on API version 2.4
	/*
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
	public @ResponseBody List<ServiceInstance> getServiceInstances() {
		logger.debug("GET: " + BASE_PATH + ", getServiceInstances()");
		return service.getAllServiceInstances();
	}
	*/
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
	public String helloWord()
	{
		return "hello world";
	}
		
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException,
			Exception {
		logger.debug("PUT: " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
		//TODO: Check to see if the planId exists. Note: we do not use the service definitionId
		/*
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		*/
		ServiceInstance instance = serviceInstanceService.createServiceInstance(
				serviceInstanceId, 
				request.getServiceDefinitionId(),
				request.getPlanId(),
				request.getOrganizationGuid(), 
				request.getSpaceGuid());
		logger.debug("ServiceInstance Created: " + instance.getId());
        return new ResponseEntity<CreateServiceInstanceResponse>(
        		new CreateServiceInstanceResponse(instance), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException, Exception {
		logger.debug( "DELETE: " + BASE_PATH + "/{instanceId}" 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = serviceInstanceService.deleteServiceInstance(instanceId);
		if (instance == null) {
			return new ResponseEntity<String>("{}", HttpStatus.NOT_FOUND);
		}
		logger.debug("ServiceInstance Deleted: " + instance.getId());
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceExistsException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
}
