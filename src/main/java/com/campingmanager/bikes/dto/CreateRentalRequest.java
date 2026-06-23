package com.campingmanager.bikes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRentalRequest {

    @NotNull(message = "L'id della bici è obbligatorio")
    private Long bikeId;

    @NotNull(message = "La data di inizio è obbligatoria")
    private LocalDate startDate;

    @NotNull(message = "La data di fine è obbligatoria")
    private LocalDate endDate;
}
