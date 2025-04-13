package ru.edi.edi_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.javavit.proxy", "ru.edi.edi_integration"})
@SpringBootApplication
public class EdiIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EdiIntegrationApplication.class, args);
    }
}
