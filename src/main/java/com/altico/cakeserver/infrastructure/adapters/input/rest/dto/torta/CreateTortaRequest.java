package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateTortaRequest(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 3, max = 255, message = "La descripción debe tener entre 3 y 255 caracteres")
        String descripcion,

        @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
        String imagenPrincipal,

        Set<Integer> ocasionIds
) {}