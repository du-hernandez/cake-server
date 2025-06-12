package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TortaOcasionRequest(
        @NotNull(message = "{ocasion.id.mandatory}")
        @Positive(message = "{ocasion.id.positive}")
        Integer ocasionId
) {}