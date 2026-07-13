package com.management.water.waterconfig;

import com.management.water.waterconfig.controller.ApartmentSourceConfigController;
import com.management.water.waterconfig.controller.WaterRateController;
import com.management.water.waterconfig.controller.WaterSourceController;
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
    "spring.datasource.url=jdbc:h2:mem:waterconfig_app_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
public class WaterConfigServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApartmentSourceConfigController apartmentSourceConfigController;

    @Autowired
    private WaterRateController waterRateController;

    @Autowired
    private WaterSourceController waterSourceController;

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext, "The application context should be successfully loaded");
        assertNotNull(apartmentSourceConfigController, "ApartmentSourceConfigController bean should be successfully injected");
        assertNotNull(waterRateController, "WaterRateController bean should be successfully injected");
        assertNotNull(waterSourceController, "WaterSourceController bean should be successfully injected");
    }

    @Test
    public void mainRuns() {
        try (var mockedSpringApp = org.mockito.Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
            mockedSpringApp.when(() -> org.springframework.boot.SpringApplication.run(WaterConfigServiceApplication.class, new String[]{}))
                    .thenReturn(null);

            WaterConfigServiceApplication.main(new String[]{});

            mockedSpringApp.verify(() -> org.springframework.boot.SpringApplication.run(WaterConfigServiceApplication.class, new String[]{}));
        }
    }
}
