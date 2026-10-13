package com.frosted.network_troubleshooting.dto;

import jakarta.validation.constraints.NotBlank;

public record AddFactRequest(
        @NotBlank
        String factName,

        @NotBlank
        String factValue
) {
}
