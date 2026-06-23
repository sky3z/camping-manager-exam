package com.campingmanager.stays.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// dati raccolti al banco quando l'ospite arriva
@Data
public class CheckInRequest {

    @NotBlank(message = "Il numero del documento è obbligatorio")
    private String documentNumber;

    private String phone;
}
