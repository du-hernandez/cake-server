package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateTortaRequest(
        @NotBlank(message = "{torta.descripcion.notblank}")
        @Size(min = 3, max = 255, message = "{torta.descripcion.size}")
        String descripcion,

        @Size(max = 500, message = "{torta.imagen.size}")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "{imagen.url.pattern}")
        String imagenPrincipal,

        Set<Integer> ocasionIds
) {}