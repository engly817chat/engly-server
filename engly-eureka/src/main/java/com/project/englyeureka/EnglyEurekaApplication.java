package com.project.englyeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EnglyEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglyEurekaApplication.class, args);
    }

}
