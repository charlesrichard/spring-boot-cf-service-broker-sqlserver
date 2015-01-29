package com.cloudfoundry.community.broker.universal.service;

import java.util.Map;
/**
 * Handles the catalog of services made available by this 
 * broker.
 * 
 * @author csvoboda@pivotal.io
 */
public interface CatalogService {
	/**
	 * @return The catalog of services provided by this broker.
	 */
	Map<String, Object> getCatalog();
}
