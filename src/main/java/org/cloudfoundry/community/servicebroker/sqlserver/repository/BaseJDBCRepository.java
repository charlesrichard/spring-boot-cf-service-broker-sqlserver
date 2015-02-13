package org.cloudfoundry.community.servicebroker.sqlserver.repository;

import org.cloudfoundry.community.servicebroker.sqlserver.constants.EnvironmentVarConstants;
import org.cloudfoundry.community.servicebroker.util.FormattedVariableList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public abstract class BaseJDBCRepository {
	protected JdbcTemplate adminDatabase;
	protected JdbcTemplate instanceDatabase;
	
	protected JdbcTemplate createJDBCTemplate(String driverName, String url) throws Exception
	{
		DriverManagerDataSource ds = new DriverManagerDataSource(); 
		ds.setDriverClassName(driverName);
		ds.setUrl(url);
		return new JdbcTemplate(ds);
	}
	
	abstract FormattedVariableList validateEnvironmentVariables();
	
	protected FormattedVariableList validateSharedEnvironmentVariables()
	{
		FormattedVariableList missingVariables = new FormattedVariableList();
		
		if(System.getenv(EnvironmentVarConstants.SERVICE_TYPE_env_key) == null)
			missingVariables.add(EnvironmentVarConstants.SERVICE_USERNAME_env_key);
		if(System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key) == null)
			missingVariables.add(EnvironmentVarConstants.SERVICE_HOST_env_key);
		if(System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key) == null)
			missingVariables.add(EnvironmentVarConstants.SERVICE_PORT_env_key);
		if(System.getenv(EnvironmentVarConstants.SERVICE_USERNAME_env_key) == null)
			missingVariables.add(EnvironmentVarConstants.SERVICE_USERNAME_env_key);
		if(System.getenv(EnvironmentVarConstants.SERVICE_PASSWORD_env_key) == null)
			missingVariables.add(EnvironmentVarConstants.SERVICE_PASSWORD_env_key);
		
		return missingVariables;
	}
	
	public String getHost()
	{
		return System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key);
	}
	
	public String getPort()
	{
		return System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key);
	}
}
