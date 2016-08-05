package com.home.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * Created by vitaliy on 7/26/16.
 */
@SpringBootApplication
public class SpringBaseServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBaseServicesApplication.class, args);
    }
}
