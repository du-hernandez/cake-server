package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.Size;

public record UpdateTortaRequest(
        @Size(min = 3, max = 255, message = "{torta.descripcion.notblank}")
        String descripcion,

        @Size(max = 500, message = "{torta.imagen.size}")
        String imagenPrincipal
) {}