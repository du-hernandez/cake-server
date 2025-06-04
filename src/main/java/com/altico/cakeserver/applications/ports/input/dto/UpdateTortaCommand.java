package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateTortaCommand(
        @Size(min = 3, max = 255, message = "{torta.descripcion.size}")
        String descripcion,

        @Size(max = 500, message = "{torta.imagen.size}")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "{imagen.url.pattern}")
        String imagen
) {}
