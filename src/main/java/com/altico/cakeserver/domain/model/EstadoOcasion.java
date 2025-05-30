package com.altico.cakeserver.domain.model;

// Enum para el estado
public enum EstadoOcasion {
    ACTIVO(1),
    INACTIVO(0);

    private final int valor;

    EstadoOcasion(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static EstadoOcasion fromValor(int valor) {
        for (EstadoOcasion estado : values()) {
            if (estado.valor == valor) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Valor de estado inválido: " + valor);
    }
}
