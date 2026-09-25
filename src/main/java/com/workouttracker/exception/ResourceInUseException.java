package com.workouttracker.exception;

/*
 * Thrown when a resource cannot be changed or deleted because
 * other data still depends on it (HTTP 409).
 */
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }
}
