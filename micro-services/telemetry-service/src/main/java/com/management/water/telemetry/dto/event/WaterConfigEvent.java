package com.management.water.telemetry.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterConfigEvent implements Serializable {

    private String eventType;      // e.g. "RATE_UPDATED", "RATE_DELETED", "CONFIG_UPDATED", "CONFIG_DELETED"
    private String entityType;     // e.g. "WATER_RATE", "APARTMENT_SOURCE_CONFIG"
    private Long entityId;
    private Long sourceId;
    private Long apartmentId;
    private long timestamp;
}
