package org.cloudfoundry.community.servicebroker.sqlserver.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cloudfoundry.community.servicebroker.controller.ServiceInstanceController;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.cloudfoundry.community.servicebroker.sqlserver.service.SqlServerServiceInstanceService;
import org.cloudfoundry.community.servicebroker.sqlserver.test.fixture.CatalogServiceFixture;
import org.cloudfoundry.community.servicebroker.sqlserver.test.fixture.ServiceFixture;
import org.cloudfoundry.community.servicebroker.sqlserver.test.fixture.ServiceInstanceFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class ServiceInstanceControllerIntegrationTest {
		
	@Autowired
    private WebApplicationContext ctx;
	
	MockMvc mockMvc;
	ServiceInstanceService serviceInstanceService;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		
		try {
			serviceInstanceService = new SqlServerServiceInstanceService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void serviceInstanceIsCreatedCorrectly() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
		   
	    String url = ServiceInstanceController.BASE_PATH + "/" + instance.getId();
	    String body = ServiceInstanceFixture.getCreateServiceInstanceRequestJson();
	    
	    mockMvc.perform(
	    		put(url)
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(), instance.getPlanId());
 	}
	
	@Test
	public void duplicateServiceInstanceCreationFails() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    
	    String url = ServiceInstanceController.BASE_PATH + "/" + instance.getId();
	    String body = ServiceInstanceFixture.getCreateServiceInstanceRequestJson();
	    
	    serviceInstanceService.createServiceInstance(ServiceFixture.getService(), instance.getId(),
	    		instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    mockMvc.perform(
	    		put(url)
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isConflict());
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(), instance.getPlanId());
 	}
	
	@Test
	public void serviceInstanceIsDeletedSuccessfully() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
			
	    String createUrl = ServiceInstanceController.BASE_PATH + "/" + instance.getId();
	    String body = ServiceInstanceFixture.getCreateServiceInstanceRequestJson();
	    
	    String deleteUrl = ServiceInstanceController.BASE_PATH + "/" + instance.getId() 
	    		+ "?service_id=" + instance.getServiceDefinitionId()
	    		+ "&plan_id=" + instance.getPlanId();
	   
	    mockMvc.perform(
	    		put(createUrl)
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceService.getServiceInstance(instance.getId());
	    
	    mockMvc.perform(delete(deleteUrl)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
 	}
	
	@Test
	public void deleteUnknownServiceInstanceFails() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstanceTwo();
			    
	    String url = ServiceInstanceController.BASE_PATH + "/" + instance.getId() 
	    		+ "?service_id=" + instance.getServiceDefinitionId()
	    		+ "&plan_id=" + instance.getPlanId();
	    
	    mockMvc.perform(delete(url)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isNotFound());
 	}
	
	@Configuration
    @EnableWebMvc
    public static class TestConfiguration {
 
        @Bean
        public ServiceInstanceController ServiceInstanceController() throws Exception {
            return new ServiceInstanceController(new SqlServerServiceInstanceService(), CatalogServiceFixture.getCatalog());
        }
    }
}
