package org.cloudfoundry.community.servicebroker.sqlserver.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceBindingFixture;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceFixture;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.cloudfoundry.community.servicebroker.sqlserver.service.SqlServerServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.sqlserver.service.SqlServerServiceInstanceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class ServiceInstanceBindingControllerIntegrationTest {

	private static final String BASE_PATH = "/v2/service_instances/" 
			+ ServiceInstanceFixture.getServiceInstance().getId()
			+ "/service_bindings";
	
	@Mock
	private ServiceDefinition serviceDefinition;
	
	@Autowired
    private WebApplicationContext ctx;
	
	MockMvc mockMvc;
	ServiceInstanceService serviceInstanceService;
	ServiceInstanceBindingService serviceInstanceBindingService;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		
		try {
			serviceInstanceService = new SqlServerServiceInstanceService();
			serviceInstanceBindingService = new SqlServerServiceInstanceBindingService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void serviceInstanceBindingIsCreatedCorrectly() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();

	    String url = BASE_PATH + "/{bindingId}";
	    String body = ServiceInstanceBindingFixture.getServiceInstanceBindingRequestJson();
	    
	    serviceInstanceService.createServiceInstance(serviceDefinition, instance.getId(),
	    		instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    mockMvc.perform(
	    		put(url, binding.getId())
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    	)
	    	.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceBindingService.deleteServiceInstanceBinding(binding.getId(), instance, instance.getServiceDefinitionId(), instance.getPlanId());
	    serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(), instance.getPlanId());
 	}
	
	@Test
	public void unknownServiceInstanceFailsBinding() throws Exception {
	    ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
	    
	    String url = BASE_PATH + "/{bindingId}";
	    String body = ServiceInstanceBindingFixture.getServiceInstanceBindingRequestJson();
	    
	    mockMvc.perform(
	    		put(url, binding.getId())
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    	)
	    	.andDo(print())
	    	.andExpect(status().isUnprocessableEntity());
 	}
	
	@Test
	public void duplicateBindingRequestFailsBinding() throws Exception {
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		
	    String url = BASE_PATH + "/{bindingId}";
	    String body = ServiceInstanceBindingFixture.getServiceInstanceBindingRequestJson();
	    
	    serviceInstanceService.createServiceInstance(serviceDefinition, instance.getId(),
	    		instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    serviceInstanceBindingService.createServiceInstanceBinding(binding.getId(), instance, instance.getServiceDefinitionId(), instance.getPlanId(), binding.getAppGuid());
	    
	    mockMvc.perform(
	    		put(url, binding.getId())
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    	)
	    	.andExpect(status().isConflict());
	    
	    serviceInstanceBindingService.deleteServiceInstanceBinding(binding.getId(), instance, instance.getServiceDefinitionId(), instance.getPlanId());
	    serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(), instance.getPlanId());
 	}	
	
	@Test
	public void serviceInstanceBindingIsDeletedSuccessfully() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		
	    serviceInstanceBindingService.createServiceInstanceBinding(binding.getId(), instance, instance.getServiceDefinitionId(), instance.getPlanId(), binding.getAppGuid());
	    
	    serviceInstanceBindingService.createServiceInstanceBinding(binding.getId(), instance, instance.getServiceDefinitionId(), instance.getPlanId(), binding.getAppGuid());
	    
	    String url = BASE_PATH + "/" + binding.getId() 
	    		+ "?service_id=" + instance.getServiceDefinitionId()
	    		+ "&plan_id=" + instance.getPlanId();
	    
	    mockMvc.perform(delete(url)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(), instance.getPlanId());
 	}
	
	@Test
	public void unknownServiceInstanceBindingNotDeleted() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
	    
	    String url = BASE_PATH + "/" + binding.getId() 
	    		+ "?service_id=" + instance.getServiceDefinitionId()
	    		+ "&plan_id=" + instance.getPlanId();
	    
	    mockMvc.perform(delete(url)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isNotFound());
 	}
}
