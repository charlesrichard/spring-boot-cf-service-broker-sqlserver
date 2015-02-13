package com.cloudfoundry.community.broker.universal.test.integration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.cloudfoundry.community.broker.universal.controller.*;
import com.cloudfoundry.community.broker.universal.model.ServiceInstance;
import com.cloudfoundry.community.broker.universal.service.*;
import com.cloudfoundry.community.broker.universal.test.fixture.ServiceInstanceFixture;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class DashboardControllerIntegrationTest {
	
	@Autowired
    private WebApplicationContext ctx;
	MockMvc mockMvc;

	ServiceInstanceService serviceInstanceService;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	}
	
	@Test
	public void dashboardIsRetrievedCorrectly() throws Exception {
	
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    
	    String url = DashboardController.BASE_PATH + "/" + instance.getId();
	    
	    serviceInstanceService.createServiceInstance(instance.getId(),
	    		instance.getServiceDefinitionId(), instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
		
		MvcResult result = this.mockMvc.perform(get(url)
		        .accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
	            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	            .andReturn();
	    
	    // TO DO - check rest of the json including plans
		String content = result.getResponse().getContentAsString();
		
		serviceInstanceService.deleteServiceInstance(instance.getId());
		
		assertNotNull(content);
	}
	
    @Configuration
    @EnableWebMvc
    public static class TestConfiguration {
 
        @Bean
        public DashboardController dashboardController() throws Exception {
            return new DashboardController();
        }
        
        @Bean
        public ServiceInstanceController ServiceInstanceController() throws Exception {
            return new ServiceInstanceController();
        }
    }
}
