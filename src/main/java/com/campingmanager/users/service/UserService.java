package com.campingmanager.users.service;

import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    // restituisco i dati dell'utente che ha fatto la richiesta
    public UserResponseDTO getMe(User currentUser) {
        return UserResponseDTO.from(currentUser);
    }
}
