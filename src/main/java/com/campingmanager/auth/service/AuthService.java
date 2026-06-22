package com.campingmanager.auth.service;

import com.campingmanager.auth.dto.AuthResponse;
import com.campingmanager.auth.dto.LoginRequest;
import com.campingmanager.security.JwtUtil;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        // se le credenziali sono sbagliate qui parte BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        return new AuthResponse(jwtUtil.generateToken(user), user.getEmail(), user.getRole().name());
    }
}
