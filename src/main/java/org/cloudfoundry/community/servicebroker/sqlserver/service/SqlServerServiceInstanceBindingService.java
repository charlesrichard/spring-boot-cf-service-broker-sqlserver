package org.cloudfoundry.community.servicebroker.sqlserver.service;

import java.util.*;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.sqlserver.constants.IdentifierConstants;
import org.cloudfoundry.community.servicebroker.sqlserver.repository.RepositoryResponse;
import org.cloudfoundry.community.servicebroker.sqlserver.repository.SqlServerRepository;
import org.cloudfoundry.community.servicebroker.util.FormattedVariableList;
import org.cloudfoundry.community.servicebroker.util.RandomString;
import org.springframework.stereotype.Service;

@Service
public class SqlServerServiceInstanceBindingService implements ServiceInstanceBindingService 
{
	private static SqlServerRepository adminRepo;
	public static final String DRDW_USERNAME_SUFFIX = "_drdw";
	
	public SqlServerServiceInstanceBindingService() throws Exception {
		adminRepo = new SqlServerRepository();
		FormattedVariableList missingEnvironmentVariables = adminRepo.validateEnvironmentVariables();
		
		if(!missingEnvironmentVariables.isEmpty())
			throw new IllegalStateException("The following required environment variables are missing: " + missingEnvironmentVariables.toString());
	}
	
	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			String bindingId, ServiceInstance instance, String serviceId, String planId, String applicationId)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException
	{		
		// Really sucks we have to make this call, but it is the only way to get the dbname out!
		RepositoryResponse repositoryInstance = null;
		try {
			repositoryInstance = adminRepo.getInstance(instance.getId());
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		String instanceId = instance.getId();
		String serviceDefinitionId = instance.getServiceDefinitionId();
		String databaseName = repositoryInstance.getName();
		String username = databaseName + DRDW_USERNAME_SUFFIX;
		String password = RandomString.generateRandomString(IdentifierConstants.RANDOM_STRING_LENGTH);
		String host = adminRepo.getHost();
		String port = adminRepo.getPort();
		String uri = adminRepo.getInstanceConnectionString(databaseName, username, password);
		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put(IdentifierConstants.CREDENTIALS_NAME, databaseName);
		credentials.put(IdentifierConstants.CREDENTIALS_HOST, host);
		credentials.put(IdentifierConstants.CREDENTIALS_PORT, port);
		credentials.put(IdentifierConstants.CREDENTIALS_USERNAME, username);
		credentials.put(IdentifierConstants.CREDENTIALS_PASSWORD, password);
		credentials.put(IdentifierConstants.CREDENTIALS_URI, uri);
		
		try {
			adminRepo.createDrDwUser(databaseName, username, password);
			adminRepo.registerBinding(instanceId, bindingId, applicationId, username, password);
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		return new ServiceInstanceBinding(instanceId, serviceDefinitionId, credentials, null, applicationId);
	}
	
	public ServiceInstanceBinding getServiceInstanceBinding(String instanceId, String bindingId) throws Exception
	{
		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance == null) // no instances were returned, so throw an exception
			return null; 
		
		RepositoryResponse binding = adminRepo.getBinding(instanceId, bindingId);
		if(binding == null)
			return null; // none are found, so return null
		
		String databaseName = instance.getName();
		String username = binding.getBindingUsername();
		String password = binding.getBindingPassword();
		String applicationId = binding.getApplicationId();
		String host = adminRepo.getHost();
		String port = adminRepo.getPort();
		String uri = adminRepo.getInstanceConnectionString(databaseName, username, password);
		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put(IdentifierConstants.CREDENTIALS_NAME, databaseName);
		credentials.put(IdentifierConstants.CREDENTIALS_HOST, host);
		credentials.put(IdentifierConstants.CREDENTIALS_PORT, port);
		credentials.put(IdentifierConstants.CREDENTIALS_USERNAME, username);
		credentials.put(IdentifierConstants.CREDENTIALS_PASSWORD, password);
		credentials.put(IdentifierConstants.CREDENTIALS_URI, uri);
		
		return new ServiceInstanceBinding(instanceId, bindingId, credentials, null, applicationId);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(String bindingId, ServiceInstance instance, 
			String serviceId, String planId) throws ServiceBrokerException
	{
		ServiceInstanceBinding binding = null;
		try {
			binding = getServiceInstanceBinding(instance.getId(), bindingId);
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		if(binding == null)
			return null;
		
		try {
			adminRepo.deleteBinding(instance.getId(), bindingId);
			adminRepo.dropUser(binding.getCredentials().get(IdentifierConstants.CREDENTIALS_USERNAME).toString());
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		// since the deletion was successful, return the bindinginstance
		return new ServiceInstanceBinding(instance.getId(), bindingId, null, null, null);
	}
}
