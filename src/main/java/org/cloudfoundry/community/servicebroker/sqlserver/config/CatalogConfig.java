package org.cloudfoundry.community.servicebroker.sqlserver.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfig {
	
	@Bean
	public Catalog catalog() {		
		return new Catalog( Arrays.asList(
				new ServiceDefinition(
					"p-sqlserver", 
					"3101b971-1044-4816-a7ac-9ded2e028079", 
					"SQLServer 2012 service for application development and testing", 
					true, 
					false,
					Arrays.asList(
							new Plan("50mb", 
									"2451fa22-df16-4c10-ba6e-1f682d3dcdc9", 
									"Shared SQLServer, 50mb persistent disk, 40 max concurrent connections",
									getPlanMetadata())),
					Arrays.asList("sqlserver", "relational"),
					getServiceDefinitionMetadata(),
					null,
					null)));
	}
	
/* Used by Pivotal CF console */	
	
	private Map<String,Object> getServiceDefinitionMetadata() {
		Map<String,Object> sdMetadata = new HashMap<String,Object>();
		sdMetadata.put("displayName", "SQL Server");
		sdMetadata.put("imageUrl","http://sqlmag.com/site-files/sqlmag.com/files/uploads/2013/11/Microsoft-SQL-Server.jpg");
		sdMetadata.put("longDescription","SQL Server Service");
		sdMetadata.put("providerDisplayName","Pivotal");
		sdMetadata.put("documentationUrl","http://www.microsoft.com/en-us/server-cloud/products/sql-server/");
		sdMetadata.put("supportUrl","http://www.microsoft.com/en-us/server-cloud/products/sql-server/");
		return sdMetadata;
	}
	
	private Map<String,Object> getPlanMetadata() {		
		Map<String,Object> planMetadata = new HashMap<String,Object>();
		planMetadata.put("costs", getCosts());
		planMetadata.put("bullets", getBullets());
		return planMetadata;
	}
	
	private List<Map<String,Object>> getCosts() {
		Map<String,Object> costsMap = new HashMap<String,Object>();
		
		Map<String,Object> amount = new HashMap<String,Object>();
		amount.put("usd", new Double(0.0));
	
		costsMap.put("amount", amount);
		costsMap.put("unit", "MONTHLY");
		
		return Arrays.asList(costsMap);
	}
	
	private List<String> getBullets() {
		return Arrays.asList("Shared SQL Server server", 
				"50 MB Storage (not enforced)", 
				"40 concurrent connections (not enforced)");
	}
	
}