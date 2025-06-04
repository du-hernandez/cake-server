package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error;

public enum ApiError {
    RESOURCE_NOT_FOUND("Recurso no encontrado"),
    VALIDATION_ERROR("Error de validación"),
    BUSINESS_RULE_VIOLATION("Violación de regla de negocio"),
    DUPLICATE_RESOURCE("Recurso duplicado"),
    INVALID_REQUEST("Solicitud inválida"),
    INTERNAL_SERVER_ERROR("Error interno del servidor"),
    METHOD_NOT_ALLOWED("Método no permitido"),
    UNSUPPORTED_MEDIA_TYPE("Tipo de contenido no soportado");

    private final String defaultMessage;

    ApiError(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
