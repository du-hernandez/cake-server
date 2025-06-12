package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TortaOcasionCommand(

        @NotNull(message = "{imagen.tortaId.mandatory}")
        @Positive(message = "{imagen.tortaId.positive")
        Integer tortaId,

        @NotNull(message = "{ocasion.id.mandatory}")
        @Positive(message = "{ocasion.id.positive}")
        Integer ocasionId
) {}
