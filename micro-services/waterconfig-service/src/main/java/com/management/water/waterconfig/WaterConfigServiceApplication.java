package com.management.water.waterconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WaterConfigServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WaterConfigServiceApplication.class, args);
  }
}
