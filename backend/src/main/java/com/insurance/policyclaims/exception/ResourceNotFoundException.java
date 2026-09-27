package com.insurance.policyclaims.exception;

/**
 * A custom exception, thrown when someone asks for e.g. a Policy with an ID
 * that doesn't exist. We catch this in GlobalExceptionHandler and turn it
 * into a clean 404 response instead of a scary Java stack trace.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
