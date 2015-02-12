package com.cloudfoundry.community.broker.universal.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.cloudfoundry.community.broker.universal"})
@EnableAutoConfiguration
//@ImportResource("classpath:service.yml")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
