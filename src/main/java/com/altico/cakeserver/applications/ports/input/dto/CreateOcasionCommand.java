package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOcasionCommand(
        @NotBlank(message = "{ocasion.nombre.notblank}")
        @Size(min = 3, max = 255, message = "{ocasion.nombre.size}")
        String nombre
) {}

