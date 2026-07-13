package com.management.water.property;

import com.management.water.property.controller.ApartmentController;
import com.management.water.property.controller.ApartmentTypeController;
import com.management.water.property.controller.BlockController;
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
    "spring.datasource.url=jdbc:h2:mem:property_app_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
public class PropertyServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApartmentController apartmentController;

    @Autowired
    private ApartmentTypeController apartmentTypeController;

    @Autowired
    private BlockController blockController;

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext, "The application context should be successfully loaded");
        assertNotNull(apartmentController, "ApartmentController bean should be successfully injected");
        assertNotNull(apartmentTypeController, "ApartmentTypeController bean should be successfully injected");
        assertNotNull(blockController, "BlockController bean should be successfully injected");
    }

    @Test
    public void mainRuns() {
        try (var mockedSpringApp = org.mockito.Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
            mockedSpringApp.when(() -> org.springframework.boot.SpringApplication.run(PropertyServiceApplication.class, new String[]{}))
                    .thenReturn(null);

            PropertyServiceApplication.main(new String[]{});

            mockedSpringApp.verify(() -> org.springframework.boot.SpringApplication.run(PropertyServiceApplication.class, new String[]{}));
        }
    }
}
