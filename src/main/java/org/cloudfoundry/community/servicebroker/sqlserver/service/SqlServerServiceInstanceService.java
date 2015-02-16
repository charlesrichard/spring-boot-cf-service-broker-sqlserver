package org.cloudfoundry.community.servicebroker.sqlserver.service;

import javax.servlet.http.HttpServletRequest;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.cloudfoundry.community.servicebroker.sqlserver.constants.IdentifierConstants;
import org.cloudfoundry.community.servicebroker.sqlserver.repository.RepositoryResponse;
import org.cloudfoundry.community.servicebroker.sqlserver.repository.SqlServerRepository;
import org.cloudfoundry.community.servicebroker.util.FormattedVariableList;
import org.cloudfoundry.community.servicebroker.util.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class SqlServerServiceInstanceService implements ServiceInstanceService{	
	private static SqlServerRepository adminRepo;
	public static final String DBO_USERNAME_SUFFIX = "_dbo";
	
	public SqlServerServiceInstanceService() throws Exception {
		adminRepo = new SqlServerRepository();
		FormattedVariableList missingEnvironmentVariables = adminRepo.validateEnvironmentVariables();
		
		if(!missingEnvironmentVariables.isEmpty())
			throw new IllegalStateException("The following required environment variables are missing: " + missingEnvironmentVariables.toString());
	}
	
	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition serviceDefinition, String instanceId,
			String planId, String organizationId, String spaceId)
			throws ServiceInstanceExistsException, ServiceBrokerException {

		RepositoryResponse instance;
		try {
			instance = adminRepo.getInstance(instanceId);
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		if(instance != null) // one already exists, so we need to return an exception
			throw new ServiceInstanceExistsException(new ServiceInstance(instanceId, instanceId, null, null, null, null));
		
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(IdentifierConstants.RANDOM_STRING_LENGTH);
		String username = databaseName + DBO_USERNAME_SUFFIX;
		String password = RandomString.generateRandomString(IdentifierConstants.RANDOM_STRING_LENGTH);
		try {
			adminRepo.createDatabase(databaseName);
			adminRepo.performSmokeTest(databaseName, true);
			adminRepo.cleanupSmokeTest(databaseName, true);
			adminRepo.registerInstance(instanceId, organizationId, spaceId, serviceDefinition.getId(), planId, databaseName, username, password);
			adminRepo.createDboUser(databaseName, username, password);
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		return new ServiceInstance(instanceId, databaseName, planId, 
				organizationId, spaceId, getDashboardUrl(instanceId));
	}

	public ServiceInstance getServiceInstance(String instanceId) {
		RepositoryResponse instance = null;
		try {
			instance = adminRepo.getInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if(instance == null) // the instance does not exist so we return null
			return null;
		
		return new ServiceInstance(instance.getInstanceId(), instance.getName(), 
				instance.getPlanId(), 
				instance.getOrganizationId(), instance.getSpaceId().toString(), getDashboardUrl(instanceId));
	}

	@Override
	public ServiceInstance deleteServiceInstance(String instanceId, String serviceId, String planId)
			throws ServiceBrokerException {
		
		//check to see if there is the service instance exists
		RepositoryResponse instance = null;
		try {
			instance = adminRepo.getInstance(instanceId);
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		if(instance == null) // instance does not exist, so there is nothing to delete
			return null; // the instance does not exist
		
		// the instance exists, so delete it 
		try {
			adminRepo.deleteInstance(instanceId);
			adminRepo.dropDboUser(instance.getInstanceUsername());
			adminRepo.dropDatabase(instance.getName());
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		
		// expected return is of this type
		return new ServiceInstance(instanceId, instanceId, null, null, null, null);
	}
	
	@Override
	public ServiceInstance updateServiceInstance(String arg0, String arg1)
			throws ServiceInstanceUpdateNotSupportedException,
			ServiceBrokerException, ServiceInstanceDoesNotExistException {
		
		throw new ServiceInstanceUpdateNotSupportedException("Method not yet implemented.");
	}
	
	// I hate this method, but this is how we are going to craft the dashboard URL
	private String getDashboardUrl(String instanceid)
	{
		HttpServletRequest request;
		
		// purpose of this try/catch is for unit testing where a servlet context may not exist
		try
		{
			request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		return request.getScheme() + "://" + request.getServerName() + IdentifierConstants.DASHBOARD_BASE_PATH + "/" + instanceid;
	}
}
