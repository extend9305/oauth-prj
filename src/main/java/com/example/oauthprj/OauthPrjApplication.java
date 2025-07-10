package com.example.oauthprj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.oauthprj.config.oauth2.properties")
public class OauthPrjApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthPrjApplication.class, args);
    }

}
