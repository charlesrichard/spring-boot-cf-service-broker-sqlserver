package com.cloudfoundry.community.broker.universal.test.unit;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cloudfoundry.community.broker.universal.constants.IdentifierConstants;
import com.cloudfoundry.community.broker.universal.repository.RepositoryResponse;
import com.cloudfoundry.community.broker.universal.repository.SQLServerRepository;
import com.cloudfoundry.community.broker.universal.util.FormattedVariableList;
import com.cloudfoundry.community.broker.universal.util.RandomString;

/**
 * @author Chuck Svoboda
 *
 */
public class SQLRepositoryTest {

	private static final String PLAN_ID = "5";
	private static final String SERVICE_DEFINITION_ID = "2313213132132";
	private static final int RANDOM_STRING_LENGTH = 5;
	private static final String FIXTURE_PASSWORD = "Password1234";
	private static SQLServerRepository repo;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		repo = new SQLServerRepository();
		FormattedVariableList missingEnvironmentVariables = repo.validateEnvironmentVariables();
		
		if(!missingEnvironmentVariables.isEmpty())
			fail(missingEnvironmentVariables.toString());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		repo = null;
	}

	@Test
	public void createAndDropInstanceDatabase() throws Exception {
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(RANDOM_STRING_LENGTH);
		
		try
		{
			repo.createDatabase(databaseName);
			repo.performSmokeTest(databaseName, true);
			repo.cleanupSmokeTest(databaseName, true);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
		
		try
		{
			repo.dropDatabase(databaseName);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void createAndDeleteInstanceUser() throws Exception {
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(RANDOM_STRING_LENGTH);
		String username = databaseName + IdentifierConstants.USERNAME_SUFFIX;
		
		try
		{
			repo.createDatabase(databaseName);
			repo.createUser(databaseName, username, FIXTURE_PASSWORD);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
		
		try
		{
			repo.dropDatabase(databaseName);
			repo.dropUser(username);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void registerAndDeleteInstance() throws Exception
	{
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(RANDOM_STRING_LENGTH);
		String instanceId = UUID.randomUUID().toString();
		String organizationId = UUID.randomUUID().toString();
		String spaceId = UUID.randomUUID().toString();
		
		try
		{
			repo.createDatabase(databaseName);
			repo.registerInstance(instanceId, organizationId, spaceId, SERVICE_DEFINITION_ID, PLAN_ID, databaseName);
			RepositoryResponse instance = repo.getInstance(instanceId);
			assertEquals("InstanceIds do not match after register instance and get", instanceId, instance.getInstanceId());
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
		
		try
		{
			repo.deleteInstance(instanceId);
			repo.dropDatabase(databaseName);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void registerAndDeleteBinding() throws Exception
	{
		String databaseName = IdentifierConstants.DATABASE_NAME_PREFIX + RandomString.generateRandomString(RANDOM_STRING_LENGTH);
		String username = databaseName + IdentifierConstants.USERNAME_SUFFIX;
		String instanceId = UUID.randomUUID().toString();
		String bindingId = UUID.randomUUID().toString();
		String organizationId = UUID.randomUUID().toString();
		String spaceId = UUID.randomUUID().toString();
		String applicationId = UUID.randomUUID().toString();
		
		try
		{
			repo.createDatabase(databaseName);
			repo.registerInstance(instanceId, organizationId, spaceId, SERVICE_DEFINITION_ID, PLAN_ID, databaseName);
			repo.createUser(databaseName, username, FIXTURE_PASSWORD);
			repo.registerBinding(instanceId, bindingId, applicationId, username, FIXTURE_PASSWORD);
			RepositoryResponse binding = repo.getBinding(instanceId, bindingId);
			assertEquals("InstanceIds do not match after register binding and get", instanceId, binding.getInstanceId());
			assertEquals("BindingIds do not match after register binding and get", bindingId, binding.getBindingId());
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
		
		try
		{
			repo.deleteBinding(instanceId, bindingId);
			repo.dropUser(username);
			repo.deleteInstance(instanceId);
			repo.dropDatabase(databaseName);
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
	}
}
