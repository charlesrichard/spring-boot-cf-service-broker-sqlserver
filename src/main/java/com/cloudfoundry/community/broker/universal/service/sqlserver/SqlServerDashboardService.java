package com.cloudfoundry.community.broker.universal.service.sqlserver;

import org.stringtemplate.v4.ST;
import com.cloudfoundry.community.broker.universal.constants.*;
import com.cloudfoundry.community.broker.universal.repository.*;
import com.cloudfoundry.community.broker.universal.service.*;

public class SqlServerDashboardService implements DashboardService {
	private static final char ST_DELIMITER = '$';
	private static final String INSTANCE_NOT_FOUND_TEMPLATE = "<html><body>Instance not found.</body></html>";
	private static final String INSTANCE_FOUND_TEMPLATE = "<html>\n"
			+ "<body>\n"
			+ "	<h1>SQL Server Service Cloud Foundry Instance Dashboard</h1><br />\n"
			+ "	<b>Instance Id:</b> $INSTANCE_ID$<br />\n"
			+ "	<b>Service Definition Id:</b> $SERVICE_DEFINITION_ID$<br />\n"
			+ "	<b>Organization Id:</b> $ORGANIZATION_ID$<br />\n"
			+ "	<b>Space Id:</b> $SPACE_ID$<br />\n"
			+ "	<b>Plan Id:</b> $PLAN_ID$<br />\n"
			+ "	<b>Host:</b> $HOST$<br />\n"
			+ "	<b>Port:</b> $PORT$<br />\n"
			+ "	<b>Database Name:</b> $DATABASE$<br />\n"
			+ "	<b>DBO Username:</b> $USERNAME$<br />\n"
			+ "	<b>DBO Password:</b> $PASSWORD$<br />\n"
			+ "</body>\n"
			+ "</html>";
	
	public String getDashboard(String instanceId) {
		try {
			SQLServerRepository adminRepo = new SQLServerRepository();
			RepositoryResponse instance = adminRepo.getInstance(instanceId);
			if(instance == null)
				return INSTANCE_NOT_FOUND_TEMPLATE;
			else
			{
				ST st = new ST(INSTANCE_FOUND_TEMPLATE, ST_DELIMITER, ST_DELIMITER);
				st.add("USERNAME", instance.getInstanceUsername());
				st.add("PASSWORD", instance.getInstancePassword());
				st.add("HOST", System.getenv(EnvironmentVarConstants.SERVICE_HOST_env_key));
				st.add("PORT", System.getenv(EnvironmentVarConstants.SERVICE_PORT_env_key));
				st.add("INSTANCE_ID", instance.getInstanceId());
				st.add("SERVICE_DEFINITION_ID", instance.getServiceDefinitionId());
				st.add("ORGANIZATION_ID", instance.getOrganizationId());
				st.add("SPACE_ID", instance.getSpaceId());
				st.add("PLAN_ID", instance.getPlanId());
				return st.render();
			}
			
		} catch (Exception ex) {
			System.err.println(ex.getMessage() + " " + ex.getStackTrace());
			return INSTANCE_NOT_FOUND_TEMPLATE;
		}
	}
}
