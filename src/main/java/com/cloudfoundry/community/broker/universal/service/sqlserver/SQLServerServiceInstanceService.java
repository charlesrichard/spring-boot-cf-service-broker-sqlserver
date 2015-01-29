package com.cloudfoundry.community.broker.universal.service.sqlserver;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.exception.ServiceBrokerException;
import com.cloudfoundry.community.broker.universal.exception.ServiceInstanceExistsException;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.repository.RepositoryResponse;
import com.cloudfoundry.community.broker.universal.repository.SQLServerRepository;
import com.cloudfoundry.community.broker.universal.service.ServiceInstanceService;
import com.cloudfoundry.community.broker.universal.util.FormattedVariableList;
import com.cloudfoundry.community.broker.universal.util.RandomString;

public class SQLServerServiceInstanceService implements ServiceInstanceService{
	private static SQLServerRepository adminRepo;
	private static String DASHBOARD_URL = "http://www.microsoft.com/en-us/server-cloud/products/sql-server/";
	
	public SQLServerServiceInstanceService() throws Exception {
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
		adminRepo.createDatabase(databaseName);
		adminRepo.performSmokeTest(databaseName, true);
		adminRepo.cleanupSmokeTest(databaseName, true);
		adminRepo.registerInstance(instanceId, organizationId, spaceId, serviceDefinitionId, planId, databaseName);
		
		return new ServiceInstance(instanceId, databaseName, planId, 
				organizationId, spaceId, DASHBOARD_URL);
	}

	public ServiceInstance getServiceInstance(String instanceId) throws Exception {
		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance == null) // the instance does not exist so we return null
			return null;
		
		return new ServiceInstance(instance.getInstanceId(), instance.getName(), 
				instance.getPlanId(), 
				instance.getOrganizationId(), instance.getSpaceId().toString(), DASHBOARD_URL);
	}

	public ServiceInstance deleteServiceInstance(String instanceId)
			throws ServiceBrokerException, Exception {
		
		//check to see if there is the service instance exists
		RepositoryResponse instance = adminRepo.getInstance(instanceId);
		if(instance == null) // instance does not exist, so there is nothing to delete
			return null; // the instance does not exist
		
		// the instance exists, so delete it 
		adminRepo.deleteInstance(instanceId);
		adminRepo.dropDatabase(instance.getName());
		
		// expected return is of this type
		return new ServiceInstance(instanceId, instanceId, null, null, null, null);
	}
	
}
