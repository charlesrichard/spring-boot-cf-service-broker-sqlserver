package com.cloudfoundry.community.broker.universal.repository;

import org.stringtemplate.v4.*;

import com.cloudfoundry.community.broker.universal.constants.EnvironmentVarConstants;
import com.cloudfoundry.community.broker.universal.util.FormattedVariableList;

public class OracleRepository extends BaseJDBCRepository {
	// Env variable keys ------------------------------------------------------------------
	private static final String ORACLE_SID_env_key = "CF_SB_ORACLE_SID";
	// ------------------------------------------------------------------
	
	// Driver constants ------------------------------------------------------------------
	private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	//
	private static final String JDBC_template = "jdbc:oracle:thin:$USERNAME$/$PASSWORD$@$HOST$:$PORT$:$SID$";
	// ------------------------------------------------------------------
	
	public OracleRepository() throws Exception
	{
		ST st = new ST(JDBC_template);
		st.add("USERNAME", System.getenv(EnvironmentVarConstants.SERVICE_USERNAME_env_key));
		st.add("PASSWORD", System.getenv(EnvironmentVarConstants.SERVICE_PASSWORD_env_key));
		st.add("HOST", System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key));
		st.add("PORT", System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key));
		st.add("SID", System.getenv(ORACLE_SID_env_key));
		
		adminDatabase = createJDBCTemplate(DRIVER_CLASS, st.render());
	}
	
	public FormattedVariableList validateEnvironmentVariables()
	{
		FormattedVariableList missingVariables = new FormattedVariableList();
		
		if(System.getenv(ORACLE_SID_env_key) == null)
			missingVariables.add(ORACLE_SID_env_key);
		
		missingVariables.addAll(validateSharedEnvironmentVariables());
		return missingVariables;
	}
}
