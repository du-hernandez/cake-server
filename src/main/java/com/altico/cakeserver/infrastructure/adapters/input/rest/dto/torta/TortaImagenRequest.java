package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TortaImagenRequest(
        @NotBlank(message = "La URL de la imagen es obligatoria")
        @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
        @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "La URL no es válida")
        String url
) {}
