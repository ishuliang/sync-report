package com.helianhealth.family.he.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true)
@SpringBootApplication(scanBasePackages = "com.helianhealth.family.he")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
