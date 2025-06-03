package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOcasionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
        String nombre
) {}