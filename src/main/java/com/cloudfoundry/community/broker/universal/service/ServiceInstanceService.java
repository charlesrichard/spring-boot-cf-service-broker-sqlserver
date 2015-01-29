package com.cloudfoundry.community.broker.universal.service;

import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceExistsException;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;

/**
 * Handles instances of service definitions.
 * 
 * @author csvoboda@pivotal.io
 */
public interface ServiceInstanceService {	
	/**
	 * Create a new instance of a service
	 * @param instanceId The id of the instance provided by the CloudController
	 * @param serviceDefinitionId The id of the service from the catalog
	 * @param planId The id of the plan for this instance
	 * @param organizationId The guid of the org this instance belongs to
	 * @param spaceId The guid of the space this instance belongs to
	 * @return The newly created ServiceInstance
	 * @throws ServiceInstanceExistsException if the service instance already exists.
	 * @throws ServiceBrokerException if something goes wrong internally
	 */
	ServiceInstance createServiceInstance(String instanceId, String serviceDefinitionId, 
			String planId,
			String organizationId, String spaceId) 
			throws ServiceInstanceExistsException, ServiceBrokerException, Exception;
	
	/**
	 * @param instanceId The id of the instance to be retrieved
	 * @return The ServiceInstance with the given id or null if one does not exist
	 */
	ServiceInstance getServiceInstance(String instanceId)  throws Exception ;
	
	/**
	 * Delete and return the instance if it exists.
	 * @param id
	 * @return The delete ServiceInstance or null if one did not exist.
	 * @throws ServiceBrokerException is something goes wrong internally
	 */
	ServiceInstance deleteServiceInstance(String id) throws ServiceBrokerException, Exception;
	
}

