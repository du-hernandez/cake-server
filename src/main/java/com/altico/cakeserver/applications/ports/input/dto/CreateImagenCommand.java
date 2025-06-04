package com.altico.cakeserver.applications.ports.input.dto;

import jakarta.validation.constraints.*;

public record CreateImagenCommand(
        @NotBlank(message = "{imagen.url.notblank}")
        @Size(max = 500, message = "{imagen.url.size}")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "{imagen.url.pattern}")
        String url,

        @NotNull(message = "{imagen.tortaId.mandator}")
        @Positive(message = "{imagen.tortaId.positive}")
        Integer tortaId
) {}
