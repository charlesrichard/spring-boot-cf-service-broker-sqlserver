package com.cloudfoundry.community.broker.universal.service;

import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceBindingExistsException;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.model.ServiceInstanceBinding;

/**
 * Handles bindings to service instances.
 * 
 * @author csvoboda@pivotal.io
 */
public interface ServiceInstanceBindingService {

	/**
	 * Create a new binding to a service instance.
	 * @param instance The instance of the serviceinstance
	 * @param bindingId The id provided by the cloud controller
	 * @param applicationId The Id of the app for the binding
	 * @return
	 * @throws ServiceInstanceBindingExistsException if the same binding already exists.  
	 */
	ServiceInstanceBinding createServiceInstanceBinding(ServiceInstance instance,
			String bindingId, String applicationId)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException, Exception;
	
	/**
	 * @param bindingId The id provided by the cloud controller
	 * @param instanceId The id of the service instance
	 * @return The ServiceInstanceBinding or null if one does not exist.
	 */
	ServiceInstanceBinding getServiceInstanceBinding(String instanceId, String bindingId) throws Exception;

	/**
	 * Delete the service instance binding.  If a binding doesn't exist, 
	 * return null.
	 * @param instanceId The id of the service instance
	 * @param bindingId The id provided by the cloud controller
	 * @return The deleted ServiceInstanceBinding or null if one does not exist.
	 */
	ServiceInstanceBinding deleteServiceInstanceBinding(String instanceId, String bindingId) throws ServiceBrokerException, Exception;
	
}
