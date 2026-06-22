package com.campingmanager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// cosa restituisco dopo il login: il token e due info comode dell'utente
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
