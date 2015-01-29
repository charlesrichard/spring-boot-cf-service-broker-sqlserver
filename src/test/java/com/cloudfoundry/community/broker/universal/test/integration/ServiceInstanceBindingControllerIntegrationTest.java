package com.cloudfoundry.community.broker.universal.test.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class ServiceInstanceBindingControllerIntegrationTest {

	private static final String BASE_PATH = "/v2/service_instances/" 
			+ ServiceInstanceFixture.getServiceInstance().getId()
			+ "/service_bindings";
	
	
	@Autowired
    private WebApplicationContext ctx;
	
	MockMvc mockMvc;
	ServiceInstanceService serviceInstanceService;
	ServiceInstanceBindingService serviceInstanceBindingService;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		
		ServiceType serviceType = Enum.valueOf(ServiceType.class, System.getenv(EnvironmentVarConstants.SERVICE_TYPE_env_key));
		try {
			serviceInstanceService = ServiceInstanceServiceFactory.getInstance(serviceType);
			serviceInstanceBindingService = ServiceInstanceBindingServiceFactory.getInstance(serviceType);
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
	    
	    serviceInstanceService.createServiceInstance(instance.getId(),
	    		instance.getServiceDefinitionId(), instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    mockMvc.perform(
	    		put(url, binding.getId())
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    	)
	    	.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceBindingService.deleteServiceInstanceBinding(instance.getId(), binding.getId());
	    serviceInstanceService.deleteServiceInstance(instance.getId());
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
	    	.andExpect(status().isUnprocessableEntity())
	    	.andExpect(jsonPath("$.message", containsString(binding.getServiceInstanceId())));
 	}
	
	@Test
	public void duplicateBindingRequestFailsBinding() throws Exception {
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		
	    String url = BASE_PATH + "/{bindingId}";
	    String body = ServiceInstanceBindingFixture.getServiceInstanceBindingRequestJson();
	    
	    serviceInstanceService.createServiceInstance(instance.getId(),
	    		instance.getServiceDefinitionId(), instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    serviceInstanceBindingService.createServiceInstanceBinding(instance, binding.getId(), binding.getAppGuid());
	    
	    mockMvc.perform(
	    		put(url, binding.getId())
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.content(body)
	    	)
	    	.andExpect(status().isConflict())
	    	.andExpect(jsonPath("$.message", containsString(binding.getId())));
	    
	    serviceInstanceBindingService.deleteServiceInstanceBinding(instance.getId(), binding.getId());
	    serviceInstanceService.deleteServiceInstance(instance.getId());
 	}	
	
	@Test
	public void serviceInstanceBindingIsDeletedSuccessfully() throws Exception {
	    ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		
	    serviceInstanceService.createServiceInstance(instance.getId(),
	    		instance.getServiceDefinitionId(), instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
	    
	    serviceInstanceBindingService.createServiceInstanceBinding(instance, binding.getId(), binding.getAppGuid());
	    
	    String url = BASE_PATH + "/" + binding.getId() 
	    		+ "?service_id=" + instance.getServiceDefinitionId()
	    		+ "&plan_id=" + instance.getPlanId();
	    
	    mockMvc.perform(delete(url)
	    		.accept(MediaType.APPLICATION_JSON)
	    	)
	    	.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	    
	    serviceInstanceService.deleteServiceInstance(instance.getId());
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

	@Configuration
    @EnableWebMvc
    public static class TestConfiguration {
 
        @Bean
        public ServiceInstanceController ServiceInstanceController() throws Exception {
            return new ServiceInstanceController();
        }
        
        @Bean
        public ServiceInstanceBindingController ServiceInstanceBinningController() throws Exception {
            return new ServiceInstanceBindingController();
        }
    }
}
