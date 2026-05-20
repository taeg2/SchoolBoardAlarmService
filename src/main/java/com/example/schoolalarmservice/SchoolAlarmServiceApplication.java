package com.example.schoolalarmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SchoolAlarmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolAlarmServiceApplication.class, args);
    }

}
