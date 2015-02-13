package com.cloudfoundry.community.broker.universal.service;

/**
 * Handles the dashboard exposed by this broker
 * 
 * @author csvoboda@pivotal.io
 */
public interface DashboardService {
	/**
	 * @return The dashboard for a service
	 */
	String getDashboard(String instanceId);
}
