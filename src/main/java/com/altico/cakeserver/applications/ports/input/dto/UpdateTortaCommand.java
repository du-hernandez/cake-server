package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.Size;

public record UpdateTortaCommand(
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion,

        @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
        String imagen
) {}
