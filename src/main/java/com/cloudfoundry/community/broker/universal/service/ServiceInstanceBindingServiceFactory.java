package com.cloudfoundry.community.broker.universal.service;

import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.service.sqlserver.*;

public class ServiceInstanceBindingServiceFactory {
	public static ServiceInstanceBindingService getInstance(ServiceType serviceType) throws Exception
	{
		switch(serviceType)
		{
		case SQLSERVER:
			return new SQLServerServiceInstanceBindingService();
		case ORACLE:
			//TODO: Add oracle serviceinstancebindingservice to switch.
			throw new UnsupportedOperationException();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
