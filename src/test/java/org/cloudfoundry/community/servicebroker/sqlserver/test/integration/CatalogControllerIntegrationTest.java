package org.cloudfoundry.community.servicebroker.sqlserver.test.integration;

import org.cloudfoundry.community.servicebroker.service.impl.BeanCatalogServiceTest;
import org.cloudfoundry.community.servicebroker.sqlserver.config.CatalogConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CatalogConfig.class})
public class CatalogControllerIntegrationTest extends BeanCatalogServiceTest {
	
}
