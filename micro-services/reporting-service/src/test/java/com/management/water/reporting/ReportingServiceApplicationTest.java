package com.management.water.reporting;

import com.management.water.reporting.controller.DashboardController;
import com.management.water.reporting.controller.MonthlySummaryController;
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
    "spring.datasource.url=jdbc:h2:mem:reporting_app_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
public class ReportingServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private MonthlySummaryController monthlySummaryController;

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext, "The application context should be successfully loaded");
        assertNotNull(dashboardController, "DashboardController bean should be successfully injected");
        assertNotNull(monthlySummaryController, "MonthlySummaryController bean should be successfully injected");
    }

    @Test
    public void mainRuns() {
        try (var mockedSpringApp = org.mockito.Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
            mockedSpringApp.when(() -> org.springframework.boot.SpringApplication.run(ReportingServiceApplication.class, new String[]{}))
                    .thenReturn(null);

            ReportingServiceApplication.main(new String[]{});

            mockedSpringApp.verify(() -> org.springframework.boot.SpringApplication.run(ReportingServiceApplication.class, new String[]{}));
        }
    }
}
