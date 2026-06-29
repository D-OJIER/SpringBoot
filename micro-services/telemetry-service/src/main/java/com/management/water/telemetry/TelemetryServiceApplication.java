package com.management.water.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TelemetryServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TelemetryServiceApplication.class, args);
  }
}
