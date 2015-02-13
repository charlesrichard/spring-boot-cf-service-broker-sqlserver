package com.cloudfoundry.community.broker.universal.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloudfoundry.community.broker.universal.constants.EnvironmentVarConstants;
import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.model.ErrorMessage;
import com.cloudfoundry.community.broker.universal.service.*;

/**
 * Base controller.
 * 
 * @author csvoboda@pivotal.io
 *
 */
public abstract class BaseController {
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	protected ServiceType serviceType;
	protected ServiceInstanceBindingService serviceInstanceBindingService;
	protected ServiceInstanceService serviceInstanceService;
	protected CatalogService catalogService;
	protected DashboardService dashboardService;
	
	public BaseController()
	{
		String serviceTypeEnvVal = System.getenv(EnvironmentVarConstants.SERVICE_TYPE_env_key);
		if(serviceTypeEnvVal == null)
			throw new IllegalStateException(EnvironmentVarConstants.SERVICE_TYPE_env_key + " environment variable cannot be null or empty");
		
		serviceType = Enum.valueOf(ServiceType.class, serviceTypeEnvVal);
		
		try
		{
			InitializeController();
		} 
		catch (Exception ex)
		{
			logger.error("Error while instantiating controller: " + ex.getMessage() + " "+ ex.getStackTrace());
			Thread.currentThread().interrupt();
			return;
		}
	}
	
	public abstract void InitializeController() throws Exception;
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			HttpMessageNotReadableException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			MethodArgumentNotValidException ex, 
			HttpServletResponse response) {
	    BindingResult result = ex.getBindingResult();
	    String message = "Missing required fields:";
	    for (FieldError error: result.getFieldErrors()) {
	    	message += " " + error.getField();
	    }
		return getErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			Exception ex, 
			HttpServletResponse response) {
		logger.warn("Exception", ex);
	    return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	public ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<ErrorMessage>(new ErrorMessage(message), 
				status);
	}
}