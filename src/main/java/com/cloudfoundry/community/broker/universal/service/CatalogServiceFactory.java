package com.cloudfoundry.community.broker.universal.service;

import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.service.yaml.YamlCatalogService;

public class CatalogServiceFactory {
	public static CatalogService getInstance(ServiceType serviceType) throws Exception
	{
		switch(serviceType)
		{
		case ORACLE:
		case SQLSERVER:
			return new YamlCatalogService();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
