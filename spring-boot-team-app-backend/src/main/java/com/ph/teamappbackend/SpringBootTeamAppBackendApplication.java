package com.ph.teamappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SpringBootTeamAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTeamAppBackendApplication.class, args);
    }

}
