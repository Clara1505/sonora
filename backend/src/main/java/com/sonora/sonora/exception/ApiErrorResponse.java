package com.sonora.sonora.exception;

import java.time.Instant;

/**
 * Standard API error payload returned by exception handlers.
 *
 * @param timestamp error creation timestamp
 * @param status HTTP status code
 * @param error HTTP reason phrase
 * @param message human-readable error message
 * @param path request path that triggered the error
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
