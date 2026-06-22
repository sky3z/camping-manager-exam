package com.campingmanager.exceptions;

// richiesta non valida a livello di logica (es. file vuoto, vecchia password sbagliata) -> HTTP 400
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
