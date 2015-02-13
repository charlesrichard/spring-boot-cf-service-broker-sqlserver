package org.cloudfoundry.community.servicebroker.sqlserver.service;

import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.service.sqlserver.*;

public class DashboardServiceFactory {
	public static DashboardService getInstance(ServiceType serviceType) throws Exception
	{
		switch(serviceType)
		{
		case SQLSERVER:
			return new SqlServerDashboardService();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
