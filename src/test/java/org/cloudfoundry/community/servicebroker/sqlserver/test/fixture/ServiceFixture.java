package org.cloudfoundry.community.servicebroker.sqlserver.test.fixture;

import java.util.ArrayList;
import java.util.List;

import com.cloudfoundry.community.broker.universal.model.ServiceDefinition;

public class ServiceFixture {

	public static List<ServiceDefinition> getAllServices() {
		List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
		services.add(ServiceFixture.getService());
		return services;
	} 
	
	public static ServiceDefinition getService() {
		return new ServiceDefinition(
				"service-one-id", 
				"Service One", 
				"Description for Service One", 
				true,
				PlanFixture.getAllPlans());
	}
	
}
