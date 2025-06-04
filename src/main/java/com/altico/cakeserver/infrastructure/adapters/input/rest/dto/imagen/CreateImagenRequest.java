package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateImagenRequest(
        @NotBlank(message = "{imagen.url.notblank}")
        @Size(max = 500, message = "{imagen.url.size}")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "{imagen.url.pattern}")
        String url,

        @NotNull(message = "{imagen.tortaId.mandatory}")
        @Positive(message = "{imagen.tortaId.positive}")
        Integer tortaId
) {}
