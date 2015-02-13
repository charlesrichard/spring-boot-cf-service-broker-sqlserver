package com.cloudfoundry.community.broker.universal.repository;

import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;

import com.cloudfoundry.community.broker.universal.constants.EnvironmentVarConstants;
import com.cloudfoundry.community.broker.universal.util.FormattedVariableList;

public class SQLServerRepository extends BaseJDBCRepository{
	
	// Env variable keys ------------------------------------------------------------------
	private static final String SQL_SERVER_ADMIN_DATABASE_NAME_env_key = "CF_SB_SQL_SERVER_ADMIN_DATABASE_NAME";
	// ------------------------------------------------------------------
	
	// Driver constants ------------------------------------------------------------------
	private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String MASTER_DB = "master";
	// Define $HOST$, $PORT$, $DATABASE$, $USERNAME$, and $PASSWORD$
	private static final String JDBC_template = "jdbc:sqlserver://<HOST>:<PORT>;databasename=<DATABASE>;user=<USERNAME>;password=<PASSWORD>";
	// ------------------------------------------------------------------
	
	// Instance DB creation scripts ------------------------------------------------------------------
	// Define $MASTER_DB$ and $INSTANCE_DB$
	private static final String CREATE_DATABASE_template = "USE [<MASTER_DB>] CREATE DATABASE <INSTANCE_DB>;";
	// ------------------------------------------------------------------
	
	// Instance DB drop scripts ------------------------------------------------------------------
	// Define $MASTER_DB$ and $INSTANCE_DB$
	private static final String DROP_DATABASE_template = "USE [<MASTER_DB>] ALTER DATABASE [<INSTANCE_DB>] SET SINGLE_USER WITH ROLLBACK IMMEDIATE DROP DATABASE [<INSTANCE_DB>];";
	// ------------------------------------------------------------------
	
	// Instance registry scripts ------------------------------------------------------------------
	// Define $ADMIN_DB$, $INSTANCE_ID$, $ORGANIZATION_ID$, $INSTANCE_DB$, $SPACE_ID$, $USERNAME$, and $PASSWORD$
	private static final String REGISTER_INSTANCE_template = "USE [<ADMIN_DB>] INSERT INTO [instance] VALUES ('<INSTANCE_ID>', '<ORGANIZATION_ID>', '<SPACE_ID>', '<SERVICE_DEFINITION_ID>', '<PLAN_ID>', '<INSTANCE_DB>', '<USERNAME>','<PASSWORD>',CURRENT_TIMESTAMP);";
	// Define $ADMIN_DB$ and $INSTANCE_ID$
	private static final String GET_INSTANCE_template = "USE [<ADMIN_DB>] SELECT i.instance_id, i.organization_id, i.space_id, i.service_definition_id, i.plan_id, i.instance_db, i.username, i.password FROM [instance] i with(nolock) WHERE i.instance_id = '<INSTANCE_ID>';";
	// Define $ADMIN_DB$, and $INSTANCE_ID$ 
	private static final String DELETE_INSTANCE_template = "USE [<ADMIN_DB>] DELETE FROM [instance] WHERE instance_id = '<INSTANCE_ID>';";
	// ------------------------------------------------------------------
	
	// Binding user creation scripts ------------------------------------------------------------------
	// Define $MASTER_DB$, $INSTANCE_DB$, $INSTANCE_USERNAME$, and $INSTANCE_PASSWORD$
	private static final String CREATE_DBO_USER_template = "USE [<MASTER_DB>] CREATE LOGIN [<INSTANCE_USERNAME>] WITH PASSWORD=N'<INSTANCE_PASSWORD>', DEFAULT_DATABASE=[<INSTANCE_DB>], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF USE [<INSTANCE_DB>] CREATE USER [<INSTANCE_USERNAME>] FOR LOGIN [<INSTANCE_USERNAME>] USE [<INSTANCE_DB>] ALTER USER [<INSTANCE_USERNAME>] WITH DEFAULT_SCHEMA=[dbo] USE [<INSTANCE_DB>] ALTER ROLE [db_owner] ADD MEMBER [<INSTANCE_USERNAME>];";
	// ------------------------------------------------------------------
	
	// Binding user creation scripts ------------------------------------------------------------------
	// Define $MASTER_DB$, $INSTANCE_DB$, $INSTANCE_USERNAME$, and $INSTANCE_PASSWORD$
	private static final String CREATE_DR_DW_USER_template = "USE [<MASTER_DB>] CREATE LOGIN [<INSTANCE_USERNAME>] WITH PASSWORD=N'<INSTANCE_PASSWORD>', DEFAULT_DATABASE=[<INSTANCE_DB>], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF USE [<INSTANCE_DB>] CREATE USER [<INSTANCE_USERNAME>] FOR LOGIN [<INSTANCE_USERNAME>] USE [<INSTANCE_DB>] ALTER USER [<INSTANCE_USERNAME>] WITH DEFAULT_SCHEMA=[dbo] USE [<INSTANCE_DB>] ALTER ROLE [db_datawriter] ADD MEMBER [<INSTANCE_USERNAME>] USE [<INSTANCE_DB>] ALTER ROLE [db_datareader] ADD MEMBER [<INSTANCE_USERNAME>];";
	// ------------------------------------------------------------------
	
	// Smoke Test scripts ------------------------------------------------------------------
	// Define $INSTANCE_DB$
	private static final String CREATE_TABLE_template = "USE [<INSTANCE_DB>] CREATE TABLE [smoke_test] (ID INT) INSERT INTO smoke_test VALUES (1);";
	// Define $INSTANCE_DB$
	private static final String SMOKE_TEST_template = "USE [<INSTANCE_DB>] SELECT id FROM [smoke_test] with(nolock);";
	// Define $INSTANCE_DB$
	private static final String SMOKE_TEST_CLEANUP_template = "USE [<INSTANCE_DB>] DROP TABLE [smoke_test];";
	// ------------------------------------------------------------------
	
	// Binding user drop scripts ------------------------------------------------------------------
	// Define $MASTER_DB$ and $INSTANCE_USERNAME$
	private static final String DROP_USER_template = "USE [<MASTER_DB>] DROP LOGIN [<INSTANCE_USERNAME>];";
	// ------------------------------------------------------------------
	
	// Binding registry scripts
	// Define $ADMIN_DB$, $APPLICATION_ID$, $INSTANCE_USERNAME$, $INSTANCE_PASSWORD$, $BINDING_ID$, and $INSTANCE_ID$
	private static final String REGISTER_BINDING_template = "USE [<ADMIN_DB>] INSERT INTO [binding] VALUES ('<BINDING_ID>', '<INSTANCE_ID>', '<APPLICATION_ID>', '<INSTANCE_USERNAME>', '<INSTANCE_PASSWORD>', CURRENT_TIMESTAMP);";
	// Define $ADMIN_DB$, $BINDING_ID$, and $INSTANCE_ID$
	private static final String GET_BINDING_template = "USE [<ADMIN_DB>] SELECT i.instance_id, organization_id, space_id, instance_db, application_id, binding_id, i.username AS instance_username, i.password AS instance_password, b.username AS binding_username, b.password AS binding_password FROM [binding] b with(nolock) INNER JOIN [instance] i with(nolock) ON b.instance_id = i.instance_id WHERE b.instance_id = '<INSTANCE_ID>' AND b.binding_id = '<BINDING_ID>';";
	// Define $ADMIN_DB$, $BINDING_ID$, and $INSTANCE_ID$ 
	private static final String DELETE_BINDING_template = "USE [<ADMIN_DB>] DELETE FROM [binding]  WHERE instance_id = '<INSTANCE_ID>' AND binding_id = '<BINDING_ID>';";
	// ------------------------------------------------------------------
	
	public SQLServerRepository() throws Exception
	{
		ST st = new ST(JDBC_template);
		st.add("USERNAME", System.getenv(EnvironmentVarConstants.SERVICE_USERNAME_env_key));
		st.add("PASSWORD", System.getenv(EnvironmentVarConstants.SERVICE_PASSWORD_env_key));
		st.add("HOST", System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key));
		st.add("PORT", System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key));
		st.add("DATABASE", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		adminDatabase = createJDBCTemplate(DRIVER_CLASS, st.render());
	}
	
	public FormattedVariableList validateEnvironmentVariables()
	{
		FormattedVariableList missingVariables = new FormattedVariableList();
		
		if(System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key) == null)
			missingVariables.add(SQL_SERVER_ADMIN_DATABASE_NAME_env_key);
		
		missingVariables.addAll(validateSharedEnvironmentVariables());
		return missingVariables;
	}
	
	public String getInstanceConnectionString(String databaseName, String username, String password)
	{
		ST st = new ST(JDBC_template);
		st.add("USERNAME", username);
		st.add("PASSWORD", password);
		st.add("HOST", System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key));
		st.add("PORT", System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key));
		st.add("DATABASE", databaseName);
		return st.render();
	}
	
	public void setInstanceConnection(String databaseName, String username, String password) throws Exception
	{
		instanceDatabase = createJDBCTemplate(DRIVER_CLASS, getInstanceConnectionString(databaseName, username, password));
	}
	
	public void createDatabase(String databaseName) throws Exception
	{
		ST st = new ST(CREATE_DATABASE_template);
		st.add("MASTER_DB", MASTER_DB);
		st.add("INSTANCE_DB", databaseName);
		adminDatabase.execute(st.render());
	}
	
	public void performSmokeTest(String databaseName, boolean performAsAdmin) throws Exception
	{
		ST st = new ST(CREATE_TABLE_template);
		st.add("INSTANCE_DB", databaseName);
		
		ST st2 = new ST(SMOKE_TEST_template);
		st2.add("INSTANCE_DB", databaseName);
		
		if(performAsAdmin)
		{
			adminDatabase.execute(st.render());
			adminDatabase.execute(st2.render());
		}
		else if (instanceDatabase != null)
		{
			instanceDatabase.execute(st.render());
			instanceDatabase.execute(st2.render());
		}
		else
			throw new Exception("InstanceConnection must be instantiated if performAsAdmin is set to false");
	}
	
	public void cleanupSmokeTest(String databaseName, boolean performAsAdmin) throws Exception
	{
		ST st = new ST(SMOKE_TEST_CLEANUP_template);
		st.add("INSTANCE_DB", databaseName);
		
		if(performAsAdmin)
			adminDatabase.execute(st.render());
		else if (instanceDatabase != null)
		{
			instanceDatabase.execute(st.render());
		}
		else
			throw new Exception("InstanceConnection must be instantiated if performAsAdmin is set to false");
	}
	
	public void registerInstance(String instanceId, String organizationId, String spaceId, String serviceDefinitionId, String planId, String databaseName, String username, String password) throws Exception
	{
		ST st = new ST(REGISTER_INSTANCE_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("INSTANCE_ID", instanceId);
		st.add("ORGANIZATION_ID", organizationId);
		st.add("SPACE_ID", spaceId);
		st.add("PLAN_ID", planId);
		st.add("INSTANCE_DB", databaseName);
		st.add("SERVICE_DEFINITION_ID", serviceDefinitionId);
		st.add("USERNAME", username);
		st.add("PASSWORD", password);
		adminDatabase.execute(st.render());
	}
	
	public void dropDatabase(String databaseName) throws Exception
	{
		ST st = new ST(DROP_DATABASE_template);
		st.add("MASTER_DB", MASTER_DB);
		st.add("INSTANCE_DB", databaseName);
		adminDatabase.execute(st.render());
	}
	
	public void deleteInstance(String instanceId) throws Exception
	{
		ST st = new ST(DELETE_INSTANCE_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("INSTANCE_ID", instanceId);
		adminDatabase.execute(st.render());
	}
	
	public RepositoryResponse getInstance(String instanceId) throws Exception
	{
		ST st = new ST(GET_INSTANCE_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("INSTANCE_ID", instanceId);
		List<Map<String, Object>> result = adminDatabase.queryForList(st.render());
		
		if(result.size() < 1)
			return null;
		
		Map<String, Object> instance = result.get(0);
		return new RepositoryResponse(instance.get("instance_id").toString(), 
				instance.get("organization_id").toString(), instance.get("service_definition_id").toString(),
				instance.get("plan_id").toString(), instance.get("space_id").toString(), 
				instance.get("instance_db").toString(), instance.get("username").toString(),
				instance.get("password").toString());
	}
	
	public void createDboUser(String databaseName, String username, String password) throws Exception
	{
		ST st = new ST(CREATE_DBO_USER_template);
		st.add("MASTER_DB", MASTER_DB);
		st.add("INSTANCE_DB", databaseName);
		st.add("INSTANCE_USERNAME", username);
		st.add("INSTANCE_PASSWORD", password);
		adminDatabase.execute(st.render());
	}
	
	public void createDrDwUser(String databaseName, String username, String password) throws Exception
	{
		ST st = new ST(CREATE_DR_DW_USER_template);
		st.add("MASTER_DB", MASTER_DB);
		st.add("INSTANCE_DB", databaseName);
		st.add("INSTANCE_USERNAME", username);
		st.add("INSTANCE_PASSWORD", password);
		adminDatabase.execute(st.render());
	}
	
	public void dropUser(String username) throws Exception
	{
		ST st = new ST(DROP_USER_template);
		st.add("MASTER_DB", MASTER_DB);
		st.add("INSTANCE_USERNAME", username);
		adminDatabase.execute(st.render());
	}
	
	public void registerBinding(String instanceId, String bindingId, String applicationId, String username, String password) throws Exception
	{
		ST st = new ST(REGISTER_BINDING_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("APPLICATION_ID", applicationId);
		st.add("INSTANCE_ID", instanceId);
		st.add("INSTANCE_USERNAME", username);
		st.add("INSTANCE_PASSWORD", password);
		st.add("BINDING_ID", bindingId);
		adminDatabase.execute(st.render());
	}
	
	public RepositoryResponse getBinding(String instanceId, String bindingId) throws Exception
	{
		ST st = new ST(GET_BINDING_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("INSTANCE_ID", instanceId);
		st.add("BINDING_ID", bindingId);
		List<Map<String, Object>> result = adminDatabase.queryForList(st.render());
		
		if(result.size() < 1)
			return null;
		
		Map<String, Object> binding = result.get(0);
		return new RepositoryResponse(binding.get("instance_id").toString(), 
				binding.get("organization_id").toString(), binding.get("space_id").toString(), 
				binding.get("instance_db").toString(), binding.get("application_id").toString(), 
				binding.get("binding_id").toString(), 
				binding.get("instance_username").toString(), binding.get("instance_password").toString(), 
				binding.get("binding_username").toString(), binding.get("binding_password").toString());
	}
	
	public void deleteBinding(String instanceId, String bindingId) throws Exception
	{
		ST st = new ST(DELETE_BINDING_template);
		st.add("ADMIN_DB", System.getenv(SQL_SERVER_ADMIN_DATABASE_NAME_env_key));
		st.add("INSTANCE_ID", instanceId);
		st.add("BINDING_ID", bindingId);
		adminDatabase.execute(st.render());
	}
}
