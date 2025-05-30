package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateTortaCommand(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion,

        @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
        String imagen,

        Set<Integer> ocasionIds
) {}
