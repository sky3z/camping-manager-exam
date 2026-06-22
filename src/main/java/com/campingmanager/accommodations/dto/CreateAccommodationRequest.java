package com.campingmanager.accommodations.dto;

import com.campingmanager.accommodations.entity.AccommodationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccommodationRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    private String name;

    private String description;

    @NotNull(message = "Il tipo è obbligatorio (CHALET o PIAZZOLA)")
    private AccommodationType type;

    @Min(value = 1, message = "La capacità deve essere almeno 1")
    private int maxCapacity;

    @NotNull(message = "Il prezzo per notte è obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo non può essere negativo")
    private BigDecimal pricePerNight;
}
