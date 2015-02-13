package com.cloudfoundry.community.broker.universal.repository;

public class RepositoryResponse {
	private String name;
	private String instanceUsername;
	private String instancePassword;
	private String bindingUsername;
	private String bindingPassword;
	private String host;
	private String port;
	private String instanceId;
	private String bindingId;
	private String applicationId;
	private String planId;
	private String spaceId;
	private String organizationId;
	private String serviceDefinitionId;
	
	public RepositoryResponse()
	{
	}
	
	public RepositoryResponse(String instanceId, String bindingId, String organizationId, 
			String spaceId, String name, String applicationId, 
			String instanceUsername, String instancePassword,
			String bindingUsername, String bindingPassword)
	{
		this.instanceId = instanceId;
		this.organizationId = organizationId;
		this.spaceId = spaceId;
		this.name = name;
		this.applicationId = applicationId;
		this.bindingId = bindingId;
		this.instanceUsername = instanceUsername;
		this.instancePassword = instancePassword;
		this.bindingUsername = bindingUsername;
		this.bindingPassword = bindingPassword;
	}
	
	public RepositoryResponse(String instanceId, String organizationId, 
			String spaceId, String serviceDefinitionId, String planId, String name, String instanceUsername, String instancePassword)
	{
		this.instanceId = instanceId;
		this.organizationId = organizationId;
		this.spaceId = spaceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.name = name;
		this.instanceUsername = instanceUsername;
		this.instancePassword = instancePassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstanceUsername() {
		return instanceUsername;
	}

	public void setInstanceUsername(String instanceUsername) {
		this.instanceUsername = instanceUsername;
	}

	public String getInstancePassword() {
		return instancePassword;
	}

	public void setInstancePassword(String instancePassword) {
		this.instancePassword = instancePassword;
	}

	public String getBindingUsername() {
		return bindingUsername;
	}

	public void setBindingUsername(String bindingUsername) {
		this.bindingUsername = bindingUsername;
	}

	public String getBindingPassword() {
		return bindingPassword;
	}

	public void setBindingPassword(String bindingPassword) {
		this.bindingPassword = bindingPassword;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getBindingId() {
		return bindingId;
	}

	public void setBindingId(String bindingId) {
		this.bindingId = bindingId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setServiceDefnitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}
	
}
