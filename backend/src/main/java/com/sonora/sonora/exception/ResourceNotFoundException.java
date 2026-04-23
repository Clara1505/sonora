package com.sonora.sonora.exception;

/** Exception thrown when a requested resource does not exist. */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates the exception with a detail message.
     *
     * @param message error detail
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
