package com.management.water.modules.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlockCreateRequest {

    @NotBlank(message = "Block name is required")
    private String name;
}
