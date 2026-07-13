package com.management.water.telemetry.avro;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AvroSerializerTest {

    @Test
    public void testSerialize_NullData() {
        AvroSerializer<DailyLogAvroEvent> serializer = new AvroSerializer<>();
        byte[] result = serializer.serialize("test-topic", null);
        assertNull(result);
    }

    @Test
    public void testSerialize_Success() {
        DailyLogAvroEvent event = DailyLogAvroEvent.newBuilder()
                .setApartmentId(101L)
                .setGuestCount(3)
                .setLogDate("2026-07-13")
                .setTotalLitresConsumed(450.0)
                .build();

        AvroSerializer<DailyLogAvroEvent> serializer = new AvroSerializer<>();
        byte[] serializedBytes = serializer.serialize("test-topic", event);

        assertNotNull(serializedBytes);
        assertTrue(serializedBytes.length > 0);

        // Verify that the serialized bytes can be successfully deserialized
        AvroDeserializer<DailyLogAvroEvent> deserializer = new AvroDeserializer<>(DailyLogAvroEvent.class);
        DailyLogAvroEvent deserializedEvent = deserializer.deserialize("test-topic", serializedBytes);

        assertNotNull(deserializedEvent);
        assertEquals(101L, deserializedEvent.getApartmentId());
        assertEquals(3, deserializedEvent.getGuestCount());
        assertEquals("2026-07-13", deserializedEvent.getLogDate().toString());
        assertEquals(450.0, deserializedEvent.getTotalLitresConsumed());
    }
}
