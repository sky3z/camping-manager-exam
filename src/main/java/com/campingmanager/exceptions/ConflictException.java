package com.campingmanager.exceptions;

// operazione in conflitto con lo stato attuale (es. email già registrata) -> HTTP 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
