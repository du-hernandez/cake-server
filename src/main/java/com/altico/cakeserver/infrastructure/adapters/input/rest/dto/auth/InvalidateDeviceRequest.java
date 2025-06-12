package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import jakarta.validation.Valid;

public record InvalidateDeviceRequest(
        @Valid String deviceId
) {}
