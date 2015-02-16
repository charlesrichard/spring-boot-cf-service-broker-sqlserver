package org.cloudfoundry.community.servicebroker.sqlserver.test.fixture;

import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceFixture;
import org.cloudfoundry.community.servicebroker.service.BeanCatalogService;
import org.cloudfoundry.community.servicebroker.service.CatalogService;

public class CatalogServiceFixture {
	private static BeanCatalogService service;
	private static Catalog catalog;
	private static ServiceDefinition serviceDefinition;
	
	public static CatalogService getCatalog()
	{
		serviceDefinition = new ServiceDefinition(ServiceInstanceFixture.getServiceInstance().getId(), "Name", "Description", true, null);
		List<ServiceDefinition> defs = new ArrayList<ServiceDefinition>();
		defs.add(serviceDefinition);
		catalog = new Catalog(defs);
		service = new BeanCatalogService(catalog);
		return service;
	}
}
