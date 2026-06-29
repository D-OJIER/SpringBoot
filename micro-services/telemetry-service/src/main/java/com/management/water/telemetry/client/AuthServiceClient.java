package com.management.water.telemetry.client;

import com.management.water.telemetry.config.FeignConfig;
import com.management.water.telemetry.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth-service.url}", configuration = FeignConfig.class)
public interface AuthServiceClient {

  @GetMapping("/users/username/{username}")
  UserDto getUserByUsername(@PathVariable("username") String username);
}
