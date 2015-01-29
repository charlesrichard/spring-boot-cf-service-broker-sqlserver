package com.cloudfoundry.community.broker.universal.service;

import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.service.sqlserver.*;

public class ServiceInstanceServiceFactory {
	public static ServiceInstanceService getInstance(ServiceType serviceType) throws Exception
	{
		switch(serviceType)
		{
		case SQLSERVER:
			return new SQLServerServiceInstanceService();
		case ORACLE:
			//TODO: Add oracle serviceinstanceservice to switch.
			throw new UnsupportedOperationException();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
