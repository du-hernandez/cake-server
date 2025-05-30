package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.NotNull;

public record TortaOcasionCommand(
        @NotNull(message = "El ID de la torta es obligatorio")
        Integer tortaId,

        @NotNull(message = "El ID de la ocasión es obligatorio")
        Integer ocasionId
) {}
