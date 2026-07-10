package com.management.water.telemetry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DailyLogXml {
    @JsonProperty("logDate")
    private String logDate; // yyyy-MM-dd format

    @JsonProperty("totalLitresConsumed")
    private Double totalLitresConsumed;

    @JsonProperty("guestCount")
    private Integer guestCount;

    @JsonProperty("apartmentId")
    private Long apartmentId;
}
