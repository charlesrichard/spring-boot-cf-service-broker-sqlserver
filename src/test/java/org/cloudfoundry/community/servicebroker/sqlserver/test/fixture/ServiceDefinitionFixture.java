package org.cloudfoundry.community.servicebroker.sqlserver.test.fixture;

import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceFixture;

public class ServiceDefinitionFixture {
	public static ServiceDefinition getServiceDefinition()
	{
		return new ServiceDefinition(ServiceInstanceFixture.getServiceInstance().getServiceDefinitionId(),
				"name", "description", true, null);
	}
}
