package com.cloudfoundry.community.broker.universal.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.cloudfoundry.community.broker.universal.controller.DashboardController;

@Configuration
public class WebSecurityConfig {

	//TODO: Figure out the damn filters for RBAC
	/*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/**").hasRole("ADMIN")
                .antMatchers(DashboardController.BASE_PATH + "/**").hasRole("USER")
            .and()
            	.httpBasic();
    }*/
    
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new AuthenticationManagerBuilder(new NopPostProcessor())
	        .inMemoryAuthentication()
	        	.withUser("user").password("password").roles("USER")
	    	.and().and().build();
    }

    private static class NopPostProcessor implements ObjectPostProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public Object postProcess(Object object) {
            return object;
        }
    };
}
