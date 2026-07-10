package com.management.water.telemetry.integration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.management.water.telemetry.avro.DailyLogAvroEvent;
import com.management.water.telemetry.dto.DailyLogXml;
import com.management.water.telemetry.dto.DailyLogXmlList;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelemetryIntegrationRoutes extends RouteBuilder {

    private final KafkaTemplate<String, DailyLogAvroEvent> kafkaTemplate;
    private final XmlMapper xmlMapper = new XmlMapper();

    @Value("${integration.sftp.host}")
    private String sftpHost;

    @Value("${integration.sftp.port}")
    private String sftpPort;

    @Value("${integration.sftp.user}")
    private String sftpUser;

    @Value("${integration.sftp.password}")
    private String sftpPassword;

    @Value("${integration.sftp.directory}")
    private String sftpDirectory;

    @Value("${integration.mq.queue-name}")
    private String mqQueueName;

    @Value("${integration.kafka.topic}")
    private String kafkaTopic;

    @Override
    public void configure() throws Exception {
        // SFTP Polling endpoint connection string
        String sftpUri = String.format(
                "sftp://%s:%s/%s?username=%s&password=%s&noop=true&idempotent=true",
                sftpHost, sftpPort, sftpDirectory, sftpUser, sftpPassword);

        // ROUTE 1: SFTP -> IBM MQ
        // - Polls the SFTP directory
        // - Converts the file body into raw String text (XML)
        // - Publishes it to the IBM MQ queue
        from(sftpUri)
                .routeId("SftpToIbmMqRoute")
                .log("File received from SFTP: ${header.CamelFileName}")
                .convertBodyTo(String.class)
                .to("jms:queue:" + mqQueueName);

        // ROUTE 2: IBM MQ -> XML -> Kafka
        // - Listens to IBM MQ queue
        // - Unmarshals raw XML payload into Java objects (DailyLogXmlList)
        // - Maps each record to an Avro object and pushes it to Kafka
        from("jms:queue:" + mqQueueName)
                .routeId("IbmMqToKafkaRoute")
                .log("Processing XML message from IBM MQ")
                .process(exchange -> {
                    String xmlContent = exchange.getIn().getBody(String.class);
                    DailyLogXmlList logsList = xmlMapper.readValue(xmlContent, DailyLogXmlList.class);

                    if (logsList != null && logsList.getLogs() != null) {
                        for (DailyLogXml logXml : logsList.getLogs()) {
                            DailyLogAvroEvent avroEvent = DailyLogAvroEvent.newBuilder()
                                    .setLogDate(logXml.getLogDate())
                                    .setTotalLitresConsumed(logXml.getTotalLitresConsumed())
                                    .setGuestCount(logXml.getGuestCount())
                                    .setApartmentId(logXml.getApartmentId())
                                    .build();

                            // Send to Kafka (key is apartment ID, value is the Avro event)
                            kafkaTemplate.send(kafkaTopic, String.valueOf(avroEvent.getApartmentId()), avroEvent);
                        }
                        log.info("Successfully dispatched XML batch of size {} to Kafka topic {}",
                                logsList.getLogs().size(), kafkaTopic);
                    }
                });
    }
}
