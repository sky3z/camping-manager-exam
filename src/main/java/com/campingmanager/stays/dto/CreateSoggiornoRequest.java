package com.campingmanager.stays.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSoggiornoRequest {

    @NotNull(message = "L'id dell'alloggio è obbligatorio")
    private Long accommodationId;

    @NotBlank(message = "Il nome dell'ospite è obbligatorio")
    private String guestName;

    @Email(message = "Email non valida")
    @NotBlank(message = "L'email dell'ospite è obbligatoria")
    private String guestEmail;

    @Min(value = 1, message = "Deve esserci almeno un ospite")
    private int numGuests;

    @NotNull(message = "La data di check-in è obbligatoria")
    private LocalDate checkInDate;

    @NotNull(message = "La data di check-out è obbligatoria")
    private LocalDate checkOutDate;
}
