package com.task.water_billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WaterBillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaterBillingApplication.class, args);
	}

}
