package com.workouttracker.exception;

/*
 * Thrown when creating something that must be unique already
 * exists, e.g. a username or email (HTTP 409).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
