package com.cloudfoundry.community.broker.universal.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cloudfoundry.community.broker.universal.controller.BaseController;
import com.cloudfoundry.community.broker.universal.controller.ServiceInstanceBindingController;
import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceBindingExistsException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceDoesNotExistException;
import com.cloudfoundry.community.broker.universal.model.ErrorMessage;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.model.ServiceInstanceBinding;
import com.cloudfoundry.community.broker.universal.model.ServiceInstanceBindingRequest;
import com.cloudfoundry.community.broker.universal.model.ServiceInstanceBindingResponse;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceBindingServiceFactory;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceServiceFactory;

@RestController
public class ServiceInstanceBindingController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances/{instanceId}/service_bindings";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceBindingController.class);
		
	public ServiceInstanceBindingController() throws Exception
	{
		super();
		serviceInstanceService = ServiceInstanceServiceFactory.getInstance(serviceType);
		serviceInstanceBindingService = ServiceInstanceBindingServiceFactory.getInstance(serviceType);
	}
	
	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceBindingResponse> bindServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@Valid @RequestBody ServiceInstanceBindingRequest request) throws
			ServiceInstanceDoesNotExistException, ServiceInstanceBindingExistsException, 
			ServiceBrokerException, Exception {
		logger.debug( "PUT: " + BASE_PATH + "/{bindingId}"
				+ ", bindServiceInstance(), serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId);
		ServiceInstance instance = serviceInstanceService.getServiceInstance(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		
		ServiceInstanceBinding binding = serviceInstanceBindingService.getServiceInstanceBinding(instanceId, bindingId);
		if(binding != null)
			throw new ServiceInstanceBindingExistsException(binding);
		
		binding = serviceInstanceBindingService.createServiceInstanceBinding(
				instance, 
				bindingId,
				request.getAppGuid());
		logger.debug("ServiceInstanceBinding Created: " + binding.getId());
        return new ResponseEntity<ServiceInstanceBindingResponse>(
        		new ServiceInstanceBindingResponse(binding), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstanceBinding(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException, Exception {
		logger.debug( "DELETE: " + BASE_PATH + "/{bindingId}"
				+ ", deleteServiceInstanceBinding(),  serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstanceBinding binding = serviceInstanceBindingService.deleteServiceInstanceBinding(instanceId, bindingId);
		if (binding == null) {
			return new ResponseEntity<String>("{}", HttpStatus.NOT_FOUND);
		}
		logger.debug("ServiceInstanceBinding Deleted: " + binding.getId());
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceBindingExistsException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			Exception ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
