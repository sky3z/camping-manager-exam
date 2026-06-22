package com.campingmanager.exceptions;

// Risorsa inesistente -> HTTP 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}