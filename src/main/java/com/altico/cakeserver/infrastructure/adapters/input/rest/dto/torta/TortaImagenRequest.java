package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TortaImagenRequest(
        @NotBlank(message = "{imagen.url.notblank}")
        @Size(max = 500, message = "{imagen.url.size}")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "{torta.imagen.pattern}")
        String url
) {}
