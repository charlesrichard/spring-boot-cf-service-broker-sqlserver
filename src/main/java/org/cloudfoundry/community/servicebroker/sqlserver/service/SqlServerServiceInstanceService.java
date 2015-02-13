package org.cloudfoundry.community.servicebroker.sqlserver.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceExistsException;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.repository.RepositoryResponse;
import com.cloudfoundry.community.broker.universal.repository.SQLServerRepository;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceService;
import com.cloudfoundry.community.broker.universal.util.FormattedVariableList;
import com.cloudfoundry.community.broker.universal.util.RandomString;

public class SqlServerServiceInstanceService implements ServiceInstanceService{	
	private static SQLServerRepository adminRepo;
	public static final String DBO_USERNAME_SUFFIX = "_dbo";
	
	public SqlServerServiceInstanceService() throws Exception {
		adminRepo = new SQLServerRepository();
		FormattedVariableList missingEnvironmentVariables = adminRepo.validateEnvironmentVariables();
		
		if(!missingEnvironmentVariables.isEmpty())
			throw new IllegalStateException("The following required environment variables are missing: " + missingEnvironmentVariables.toString());
	}
	
	public ServiceInstance createServiceInstance(String instanceId,
			String serviceDefinitionId, String planId, String organizationId, String spaceId)
			throws ServiceInstanceExistsException, ServiceBrokerException, Exception {

		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance != null) // one already exists, so we need to return an exception
			throw new ServiceInstanceExistsException(new ServiceInstance(instanceId, instanceId, null, null, null, null));
		
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(IdentifierConstants.RANDOM_STRING_LENGTH);
		String username = databaseName + DBO_USERNAME_SUFFIX;
		String password = RandomString.generateRandomString(IdentifierConstants.RANDOM_STRING_LENGTH);
		adminRepo.createDatabase(databaseName);
		adminRepo.performSmokeTest(databaseName, true);
		adminRepo.cleanupSmokeTest(databaseName, true);
		adminRepo.registerInstance(instanceId, organizationId, spaceId, serviceDefinitionId, planId, databaseName, username, password);
		adminRepo.createDboUser(databaseName, username, password);
		
		return new ServiceInstance(instanceId, databaseName, planId, 
				organizationId, spaceId, getDashboardUrl(instanceId));
	}

	public ServiceInstance getServiceInstance(String instanceId) throws Exception {
		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance == null) // the instance does not exist so we return null
			return null;
		
		return new ServiceInstance(instance.getInstanceId(), instance.getName(), 
				instance.getPlanId(), 
				instance.getOrganizationId(), instance.getSpaceId().toString(), getDashboardUrl(instanceId));
	}

	public ServiceInstance deleteServiceInstance(String instanceId)
			throws ServiceBrokerException, Exception {
		
		//check to see if there is the service instance exists
		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance == null) // instance does not exist, so there is nothing to delete
			return null; // the instance does not exist
		
		// the instance exists, so delete it 
		adminRepo.deleteInstance(instanceId);
		adminRepo.dropUser(instance.getInstanceUsername());
		adminRepo.dropDatabase(instance.getName());
		
		// expected return is of this type
		return new ServiceInstance(instanceId, instanceId, null, null, null, null);
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
			System.err.println(ex.getMessage() + " " + ex.getStackTrace());
			return null;
		}
		
		return request.getScheme() + "://" + request.getServerName() + IdentifierConstants.DASHBOARD_BASE_PATH + "/" + instanceid;
	}
}
