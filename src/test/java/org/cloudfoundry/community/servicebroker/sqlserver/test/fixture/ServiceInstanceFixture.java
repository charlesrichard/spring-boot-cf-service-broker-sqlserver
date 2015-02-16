package org.cloudfoundry.community.servicebroker.sqlserver.test.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceResponse;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.fixture.DataFixture;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ServiceInstanceFixture {
	public static List<ServiceInstance> getAllServiceInstances() {
		List<ServiceInstance> instances = new ArrayList<ServiceInstance>();
		instances.add(getServiceInstance());
		instances.add(getServiceInstanceTwo());
		return instances;
	}
	
	public static ServiceInstance getServiceInstance() {
		return new ServiceInstance(
				"service-instance-one-id",  
				"3101b971-1044-4816-a7ac-9ded2e028079", 
				"2451fa22-df16-4c10-ba6e-1f682d3dcdc9", 
				DataFixture.getOrgOneGuid(), 
				DataFixture.getSpaceOneGuid(), 
				"dashboard_url");
	}
	
	public static ServiceInstance getServiceInstanceTwo() {
		return new ServiceInstance(
				"service-instance-two-id", 
				"service-two-id", 
				"plan-two-id", 
				DataFixture.getOrgOneGuid(), 
				DataFixture.getSpaceOneGuid(),
				"dashboard_url");
	}
	
	public static String getServiceInstanceId() {
		return "service-instance-id";
	}
	
	public static CreateServiceInstanceRequest getCreateServiceInstanceRequest() {
		ServiceDefinition service = ServiceFixture.getService();
		return new CreateServiceInstanceRequest(
				service.getId(), 
				service.getPlans().get(0).getId(),
				DataFixture.getOrgOneGuid(),
				DataFixture.getSpaceOneGuid()
		);
	}
	
	public static String getCreateServiceInstanceRequestJson() throws JsonGenerationException, JsonMappingException, IOException {
		 return DataFixture.toJson(getCreateServiceInstanceRequest());
	}
		
	public static CreateServiceInstanceResponse getCreateServiceInstanceResponse() {
		return new CreateServiceInstanceResponse(getServiceInstance());
	}
}
