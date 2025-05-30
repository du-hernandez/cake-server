package com.altico.cakeserver.applications.ports.input.dto;

import java.util.List;

public record FilterTortaCommand(
        String descripcion,
        List<Integer> ocasionIds,
        Boolean tieneImagen,
        Integer pagina,
        Integer tamanio
) {
    public FilterTortaCommand {
        if (pagina == null || pagina < 0) pagina = 0;
        if (tamanio == null || tamanio <= 0) tamanio = 10;
        if (tamanio > 100) tamanio = 100; // Límite máximo
    }
}
