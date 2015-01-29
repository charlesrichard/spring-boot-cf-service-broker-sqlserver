package com.cloudfoundry.community.broker.universal.test.fixture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cloudfoundry.community.broker.universal.model.*;

public class ServiceInstanceBindingFixture {

	public static ServiceInstanceBinding getServiceInstanceBinding() {
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
		return new ServiceInstanceBinding(
				instance.getId(),
				getServiceInstanceBindingId(),
				getCredentials(),
				getSysLogDrainUrl(),
				getAppGuid()
		);
	}

	public static String getServiceInstanceBindingId() {
		return "service_instance_binding_id";
	}
	
	public static Map<String,Object> getCredentials() {
		Map<String,Object> credentials = new HashMap<String,Object>();
		credentials.put("uri","uri");
		credentials.put("username", "username");
		credentials.put("password", "password");
		return credentials;
	}
	
	public static String getSysLogDrainUrl() {
		return "syslog_drain_url";
	}
	
	public static String getAppGuid() {
		return "app_guid";
	}
	
	public static ServiceInstanceBindingRequest getServiceInstanceBindingRequest() {
		return new ServiceInstanceBindingRequest(
				ServiceFixture.getService().getId(), 
				PlanFixture.getPlanOne().getId(),
				getAppGuid()
		); 	
	}
	
	public static String getServiceInstanceBindingRequestJson() throws JsonGenerationException, JsonMappingException, IOException {
		 ObjectMapper mapper = new ObjectMapper();
		 return mapper.writeValueAsString(getServiceInstanceBindingRequest());
	}
	
}
