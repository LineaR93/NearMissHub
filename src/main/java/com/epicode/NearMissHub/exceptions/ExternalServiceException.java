package com.epicode.NearMissHub.exceptions;

public class ExternalServiceException extends RuntimeException {
    public String service;

    public ExternalServiceException(String service, String message, Throwable cause) {
        super(message, cause);
        this.service = service;
    }
}
