package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TortaOcasionRequest(
        @NotNull(message = "El ID de la ocasión es obligatorio")
        @Positive(message = "El ID de la ocasión debe ser positivo")
        Integer ocasionId
) {}