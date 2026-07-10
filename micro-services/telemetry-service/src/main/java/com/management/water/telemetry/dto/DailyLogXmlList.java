package com.management.water.telemetry.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

@JacksonXmlRootElement(localName = "DailyLogs")
@Data
public class DailyLogXmlList {
    @JacksonXmlProperty(localName = "DailyLog")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DailyLogXml> logs;
}
