package com.xha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xha"})
public class DFASensitiveWordApplication {
    public static void main(String[] args) {
        SpringApplication.run(DFASensitiveWordApplication.class, args);
    }
}
