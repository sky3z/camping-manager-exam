package com.campingmanager.users.dto;

import com.campingmanager.users.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

// versione "pubblica" dell'utente: non espongo mai la password
@Data
public class UserResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String surname;
    private String profileImage;
    private String role;
    private LocalDateTime createdAt;

    // costruisco il DTO a partire dall'entità
    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setProfileImage(user.getProfileImage());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
