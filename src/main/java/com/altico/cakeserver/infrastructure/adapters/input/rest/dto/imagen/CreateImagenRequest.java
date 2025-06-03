package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateImagenRequest(
        @NotBlank(message = "La URL es obligatoria")
        @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "La URL no es válida")
        String url,

        @NotNull(message = "El ID de la torta es obligatorio")
        @Positive(message = "El ID de la torta debe ser positivo")
        Integer tortaId
) {}
