package com.TrackFile.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrackfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackfileApplication.class, args);
    }
}