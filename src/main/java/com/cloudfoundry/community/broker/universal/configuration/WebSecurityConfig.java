package com.cloudfoundry.community.broker.universal.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.cloudfoundry.community.broker.universal.controller.DashboardController;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth
        	.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN")
        	.and().withUser("user").password("password").roles("USER").and().and().build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/**").hasRole("ADMIN")
                .antMatchers(DashboardController.BASE_PATH + "/**").hasRole("USER")
            .and()
            	.httpBasic();
    }
}

/*
@Configuration
public class WebSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new AuthenticationManagerBuilder(new NopPostProcessor())
                       .inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN")
                       .and().withUser("user").password("password").roles("USER").and().and().build();
    }

    private static class NopPostProcessor implements ObjectPostProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public Object postProcess(Object object) {
            return object;
        }
    };
}
*/