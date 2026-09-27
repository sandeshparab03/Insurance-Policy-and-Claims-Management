package com.insurance.policyclaims.exception;

/**
 * Thrown when a request is technically well-formed but breaks a business rule -
 * e.g. trying to file a claim against a policy that has already expired.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
