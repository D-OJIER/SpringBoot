package com.management.water.auth;

import com.management.water.auth.controller.AuthController;
import com.management.water.auth.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:main_app_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
public class AuthServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthController authController;

    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext, "The application context should be successfully loaded");
        assertNotNull(authController, "AuthController bean should be successfully injected");
        assertNotNull(userController, "UserController bean should be successfully injected");
    }

    @Test
    public void mainRuns() {
        // Verifies that calling the main() entrypoint correctly triggers SpringApplication.run
        // with the appropriate primary source class.
        try (var mockedSpringApp = org.mockito.Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
            mockedSpringApp.when(() -> org.springframework.boot.SpringApplication.run(AuthServiceApplication.class, new String[]{}))
                    .thenReturn(null);

            AuthServiceApplication.main(new String[]{});

            mockedSpringApp.verify(() -> org.springframework.boot.SpringApplication.run(AuthServiceApplication.class, new String[]{}));
        }
    }
}
