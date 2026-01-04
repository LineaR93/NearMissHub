package com.epicode.NearMissHub.exceptions;

// Custom exceptions + centralized error handling to keep API responses consistent.

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
