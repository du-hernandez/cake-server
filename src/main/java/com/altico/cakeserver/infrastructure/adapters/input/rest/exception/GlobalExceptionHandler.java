package com.altico.cakeserver.infrastructure.adapters.input.rest.exception;

import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error.ApiError;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error.ErrorResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Excepciones de dominio - 404 Not Found
    @ExceptionHandler({
            TortaNotFoundException.class,
            OcasionNotFoundException.class,
            ImagenNotFoundException.class,
            UserNotFoundException.class,
            RoleNotFoundException.class,
            PermissionNotFoundException.class,
            RefreshTokenNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ApiError.RESOURCE_NOT_FOUND.getDefaultMessage(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Excepciones de dominio - 409 Conflict
    @ExceptionHandler({
            DuplicateOcasionException.class,
            BusinessRuleViolationException.class,
            DuplicateUserException.class,
            DuplicateRoleException.class,
            DuplicatePermissionException.class,
            RoleInUseException.class,
            PermissionInUseException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Conflicto de negocio: {}", ex.getMessage());

        ApiError errorType = ex instanceof DuplicateOcasionException
                ? ApiError.DUPLICATE_RESOURCE
                : ApiError.BUSINESS_RULE_VIOLATION;

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                errorType.getDefaultMessage(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Excepciones de dominio - 400 Bad Request
    @ExceptionHandler({
            InvalidImageException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Solicitud inválida: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ApiError.INVALID_REQUEST.getDefaultMessage(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Validación de campos - 400 Bad Request
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ValidationException.class,
            PasswordValidationException.class,
            EmailValidationException.class,
            InvalidPermissionException.class,
            InvalidRoleHierarchyException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Error de validación en: {}", request.getRequestURI());

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError
                            ? ((FieldError) error).getField()
                            : error.getObjectName();
                    Object rejectedValue = error instanceof FieldError
                            ? ((FieldError) error).getRejectedValue()
                            : null;
                    String message = error.getDefaultMessage();

                    return new ValidationError(fieldName, rejectedValue, message);
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ApiError.VALIDATION_ERROR.getDefaultMessage(),
                "Error de validación en los campos enviados",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Parámetros faltantes - 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("Parámetro faltante: {}", ex.getParameterName());

        String message = String.format("El parámetro '%s' es requerido", ex.getParameterName());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ApiError.INVALID_REQUEST.getDefaultMessage(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Tipo de argumento incorrecto - 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Tipo de argumento incorrecto: {}", ex.getName());

        String message = String.format(
                "El parámetro '%s' tiene un tipo incorrecto. Se esperaba %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "otro tipo"
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ApiError.INVALID_REQUEST.getDefaultMessage(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // JSON mal formado - 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Error al leer el mensaje HTTP: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ApiError.INVALID_REQUEST.getDefaultMessage(),
                "El formato del JSON es inválido",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Método HTTP no soportado - 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("Método HTTP no soportado: {}", ex.getMethod());

        String message = String.format(
                "El método %s no está soportado para esta ruta. Métodos soportados: %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ApiError.METHOD_NOT_ALLOWED.getDefaultMessage(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    // Tipo de contenido no soportado - 415 Unsupported Media Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("Tipo de contenido no soportado: {}", ex.getContentType());

        String message = String.format(
                "El tipo de contenido '%s' no está soportado. Tipos soportados: %s",
                ex.getContentType(),
                ex.getSupportedMediaTypes()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ApiError.UNSUPPORTED_MEDIA_TYPE.getDefaultMessage(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    // Ruta no encontrada - 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("Ruta no encontrada: {}", ex.getRequestURL());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ApiError.RESOURCE_NOT_FOUND.getDefaultMessage(),
                "La ruta solicitada no existe",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Violación de integridad de datos - 409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Violación de integridad de datos: {}", ex.getMessage());

        String message = "No se puede completar la operación debido a restricciones de integridad de datos";

        // Intentar dar un mensaje más específico
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("foreign key") || ex.getMessage().contains("fk_")) {
                message = "No se puede eliminar este registro porque tiene datos relacionados";
            } else if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                message = "Ya existe un registro con estos datos";
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ApiError.BUSINESS_RULE_VIOLATION.getDefaultMessage(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Excepciones de seguridad - 401 Unauthorized
    @ExceptionHandler({
            org.springframework.security.authentication.BadCredentialsException.class,
            org.springframework.security.authentication.InsufficientAuthenticationException.class,
            org.springframework.security.core.AuthenticationException.class,
            InvalidCredentialsException.class,
            ExpiredRefreshTokenException.class,
            RevokedRefreshTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        log.error("Error de autenticación: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Error de autenticación",
                "Credenciales inválidas o token expirado",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Excepciones de autorización - 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.error("Acceso denegado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado",
                "No tienes permisos para acceder a este recurso",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // Username ya existe - 409 Conflict
    @ExceptionHandler(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            org.springframework.security.core.userdetails.UsernameNotFoundException ex,
            HttpServletRequest request) {
        log.error("Usuario no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Error de autenticación",
                "Usuario o contraseña incorrectos",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Excepción genérica - 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Error interno del servidor", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ApiError.INTERNAL_SERVER_ERROR.getDefaultMessage(),
                "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // ============== EXCEPCIONES DE AUTORIZACIÓN ==============

    @ExceptionHandler({
            InsufficientPermissionsException.class,
            InsufficientRoleException.class
    })
    public ResponseEntity<ErrorResponse> handleInsufficientPermissionsException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Permisos insuficientes: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Permisos insuficientes",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlockedException(
            UserBlockedException ex, HttpServletRequest request) {
        log.error("Usuario bloqueado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.LOCKED.value(),
                "Usuario bloqueado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
    }

    // ============== EXCEPCIONES DE LÍMITES Y CUOTAS ==============

    @ExceptionHandler({
            TooManyTokensException.class,
            RateLimitExceededException.class,
            QuotaExceededException.class
    })
    public ResponseEntity<ErrorResponse> handleLimitExceededException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Límite excedido: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Límite excedido",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    // ============== EXCEPCIONES DE SEGURIDAD ==============

    @ExceptionHandler({
            SuspiciousActivityException.class,
            SecurityViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleSecurityException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Violación de seguridad: {}", ex.getMessage());

        // No revelar detalles de seguridad al cliente
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado",
                "Actividad sospechosa detectada",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // ============== EXCEPCIONES DE SESIÓN ==============

    @ExceptionHandler({
            SessionExpiredException.class,
            InvalidSessionException.class
    })
    public ResponseEntity<ErrorResponse> handleSessionException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Error de sesión: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Sesión inválida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ConcurrentSessionException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentSessionException(
            ConcurrentSessionException ex, HttpServletRequest request) {
        log.error("Sesión concurrente: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Sesión concurrente",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // ============== EXCEPCIONES DE SISTEMA ==============

    @ExceptionHandler({
            ConfigurationException.class,
            SystemInitializationException.class
    })
    public ResponseEntity<ErrorResponse> handleSystemException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Error de sistema: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error de sistema",
                "Error interno del sistema. Contacte al administrador.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler({
            SynchronizationException.class,
            DataConsistencyException.class
    })
    public ResponseEntity<ErrorResponse> handleDataException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Error de consistencia de datos: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error de consistencia",
                "Error en la consistencia de datos. Intente nuevamente.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // ============== EXCEPCIONES DE RECURSOS ==============

    @ExceptionHandler(ResourceLockedException.class)
    public ResponseEntity<ErrorResponse> handleResourceLockedException(
            ResourceLockedException ex, HttpServletRequest request) {
        log.error("Recurso bloqueado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.LOCKED.value(),
                "Recurso bloqueado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(
            ResourceConflictException ex, HttpServletRequest request) {
        log.error("Conflicto de recurso: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflicto de recurso",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // ============== AUDITORÍA ==============

    @ExceptionHandler(AuditLogException.class)
    public ResponseEntity<ErrorResponse> handleAuditLogException(
            AuditLogException ex, HttpServletRequest request) {
        log.error("Error en auditoría: {}", ex.getMessage(), ex);

        // Los errores de auditoría no deben interrumpir la operación principal
        // pero se debe notificar al administrador
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error de auditoría",
                "Error en el registro de auditoría",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Agregar manejo específico para errores de base de datos
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(SQLException ex) {
        // Log técnico interno
        log.error("Database error: {}", ex.getMessage(), ex);

        // Respuesta genérica al usuario
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(501, "Error de base de datos", "Error interno del sistema", "/"));
    }
}