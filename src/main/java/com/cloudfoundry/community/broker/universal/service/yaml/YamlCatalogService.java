package com.cloudfoundry.community.broker.universal.service.yaml;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.cloudfoundry.community.broker.universal.service.*;

public class YamlCatalogService implements CatalogService {
	private Map<String, Object> settings;
	
	public synchronized Map<String, Object> getCatalog() {
	    if (settings == null) {
	    	Yaml yaml = new Yaml();
	    	InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("services.yml");  
	    	settings = (Map<String, Object>)yaml.load(stream);
	    }
	    return settings;
	}
}
