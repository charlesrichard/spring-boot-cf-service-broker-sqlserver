package org.cloudfoundry.community.servicebroker.sqlserver.test.integration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.fixture.*;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.cloudfoundry.community.servicebroker.sqlserver.controller.DashboardController;
import org.cloudfoundry.community.servicebroker.sqlserver.service.SqlServerServiceInstanceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.cloudfoundry.community.servicebroker.sqlserver.service.*;
import org.cloudfoundry.community.servicebroker.sqlserver.test.fixture.ServiceFixture;


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
		
		try {
			serviceInstanceService = new SqlServerServiceInstanceService();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void dashboardIsRetrievedCorrectly() throws Exception {
	
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
	    
	    String url = DashboardController.BASE_PATH + "/" + instance.getId();
	    
	    serviceInstanceService.createServiceInstance(ServiceFixture.getService(), 
	    		instance.getId(),
	    		instance.getPlanId(), 
	    		instance.getOrganizationGuid(), instance.getSpaceGuid());
		
		MvcResult result = this.mockMvc.perform(get(url))
	            .andReturn();
	    
	    // TO DO - check rest of the json including plans
		String content = result.getResponse().getContentAsString();
		System.out.println(content);
		
		serviceInstanceService.deleteServiceInstance(instance.getId(), instance.getServiceDefinitionId(),
				instance.getPlanId());
		
		assertNotNull(content);
	}
	
	@Configuration
    @EnableWebMvc
    public static class TestConfiguration {
 
        @Bean
        public DashboardController DashboardController() throws Exception {
            return new DashboardController(new SqlServerDashboardService());
        }
    }
}
