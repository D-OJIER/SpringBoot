package com.management.water.telemetry.integration;

import com.management.water.telemetry.avro.DailyLogAvroEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class TelemetryIntegrationRoutesTest {

    @Test
    public void testRouteDefinition() throws Exception {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, DailyLogAvroEvent> kafkaTemplate = mock(KafkaTemplate.class);
        TelemetryIntegrationRoutes routes = new TelemetryIntegrationRoutes(kafkaTemplate);

        ReflectionTestUtils.setField(routes, "sftpHost", "localhost");
        ReflectionTestUtils.setField(routes, "sftpPort", "2222");
        ReflectionTestUtils.setField(routes, "sftpUser", "sftpuser");
        ReflectionTestUtils.setField(routes, "sftpPassword", "password");
        ReflectionTestUtils.setField(routes, "sftpDirectory", "upload");
        ReflectionTestUtils.setField(routes, "mqQueueName", "water.telemetry.queue");
        ReflectionTestUtils.setField(routes, "kafkaTopic", "water-logs");

        DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(routes);

        assertNotNull(context.getRouteDefinition("SftpToIbmMqRoute"));
        assertNotNull(context.getRouteDefinition("IbmMqToKafkaRoute"));
    }
}
