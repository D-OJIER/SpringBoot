package com.management.water.telemetry.avro;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AvroDeserializerTest {

    @Test
    public void testDeserialize_Success() throws IOException {
        DailyLogAvroEvent event = DailyLogAvroEvent.newBuilder()
                .setApartmentId(100L)
                .setGuestCount(2)
                .setLogDate("2026-07-10")
                .setTotalLitresConsumed(350.0)
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<DailyLogAvroEvent> writer = new SpecificDatumWriter<>(event.getSchema());
        writer.write(event, encoder);
        encoder.flush();
        byte[] serializedData = out.toByteArray();

        AvroDeserializer<DailyLogAvroEvent> deserializer = new AvroDeserializer<>(DailyLogAvroEvent.class);
        DailyLogAvroEvent deserializedEvent = deserializer.deserialize("test-topic", serializedData);

        assertNotNull(deserializedEvent);
        assertEquals(100L, deserializedEvent.getApartmentId());
        assertEquals(2, deserializedEvent.getGuestCount());
        assertEquals("2026-07-10", deserializedEvent.getLogDate().toString());
        assertEquals(350.0, deserializedEvent.getTotalLitresConsumed());
    }

    @Test
    public void testDeserialize_NullData() {
        AvroDeserializer<DailyLogAvroEvent> deserializer = new AvroDeserializer<>(DailyLogAvroEvent.class);
        DailyLogAvroEvent result = deserializer.deserialize("test-topic", null);
        assertNull(result);
    }

    @Test
    public void testDeserialize_Failure() {
        AvroDeserializer<DailyLogAvroEvent> deserializer = new AvroDeserializer<>(DailyLogAvroEvent.class);
        byte[] badData = new byte[]{1, 2, 3, 4};
        assertThrows(RuntimeException.class, () -> deserializer.deserialize("test-topic", badData));
    }
}
