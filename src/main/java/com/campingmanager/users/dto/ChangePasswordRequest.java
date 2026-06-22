package com.campingmanager.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "La password attuale è obbligatoria")
    private String oldPassword;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 8, message = "La nuova password deve avere almeno 8 caratteri")
    private String newPassword;
}
