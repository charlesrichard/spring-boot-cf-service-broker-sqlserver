package com.cloudfoundry.community.broker.universal.test.integration;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.cloudfoundry.community.broker.universal.model.*;
import com.cloudfoundry.community.broker.universal.service.*;
import com.cloudfoundry.community.broker.universal.test.fixture.*;
import com.cloudfoundry.community.broker.universal.constants.EnvironmentVarConstants;
import com.cloudfoundry.community.broker.universal.constants.ServiceType;
import com.cloudfoundry.community.broker.universal.controller.*;

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
			serviceInstanceService = ServiceInstanceServiceFactory.getInstance(Enum.valueOf(ServiceType.class, System.getenv(EnvironmentVarConstants.SERVICE_TYPE_env_key)));
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
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId());
 	}
	
	@Test
	public void duplicateServiceInstanceCreationFails() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    
	    String url = ServiceInstanceController.BASE_PATH + "/" + instance.getId();
	    String body = ServiceInstanceFixture.getCreateServiceInstanceRequestJson();
	    
	    serviceInstanceService.createServiceInstance(instance.getId(),
	    		instance.getServiceDefinitionId(), instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    mockMvc.perform(
	    		put(url)
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isConflict())
	    	.andExpect(jsonPath("$.message", containsString(instance.getId())));
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId());
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
            return new ServiceInstanceController();
        }
    }
}
