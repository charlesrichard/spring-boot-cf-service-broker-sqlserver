package org.cloudfoundry.community.servicebroker.sqlserver.test.fixture;

import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.fixture.PlanFixture;

public class ServiceFixture {
	public static ServiceDefinition getService()
	{
		return new ServiceDefinition(ServiceInstanceFixture.getServiceInstance().getServiceDefinitionId(),
				"name", "description", true, PlanFixture.getAllPlans());
	}
}
