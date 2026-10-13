package com.frosted.network_troubleshooting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSessionRequest(
        @NotNull
        @Positive
        Integer userId) {

}
