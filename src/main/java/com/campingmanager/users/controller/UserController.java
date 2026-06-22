package com.campingmanager.users.controller;

import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // l'utente loggato me lo dà Spring con @AuthenticationPrincipal (lo ha messo il filtro JWT)
    @GetMapping
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMe(currentUser));
    }
}
