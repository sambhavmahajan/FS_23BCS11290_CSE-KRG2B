package com.github.sambhavmahajan.cloudstorageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CloudStorageServiceApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CloudStorageServiceApplication.class, args);
    }
}
