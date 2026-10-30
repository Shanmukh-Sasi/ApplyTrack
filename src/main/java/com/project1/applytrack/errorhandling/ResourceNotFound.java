package com.project1.applytrack.errorhandling;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {

        super(message);
    }
}
