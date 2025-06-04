package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOcasionRequest(
        @NotBlank(message = "{ocasion.nombre.notblank}")
        @Size(min = 3, max = 255, message = "{ocasion.nombre.size}")
        String nombre
) {}