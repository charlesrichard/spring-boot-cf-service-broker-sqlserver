package org.cloudfoundry.community.servicebroker.sqlserver.service;


public class DashboardServiceFactory {
	public static DashboardService getInstance() throws Exception
	{
		return new SqlServerDashboardService();
	}
}
