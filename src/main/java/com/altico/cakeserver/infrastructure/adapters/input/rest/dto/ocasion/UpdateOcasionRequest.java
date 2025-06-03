package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

import jakarta.validation.constraints.Size;

public record UpdateOcasionRequest(
        @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
        String nombre
) {}