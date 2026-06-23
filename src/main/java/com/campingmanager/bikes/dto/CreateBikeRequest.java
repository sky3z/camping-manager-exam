package com.campingmanager.bikes.dto;

import com.campingmanager.bikes.entity.BikeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBikeRequest {

    @NotBlank(message = "Il codice è obbligatorio")
    private String code;

    private String model;

    @NotNull(message = "Il tipo è obbligatorio (FULL o MONO)")
    private BikeType type;

    @NotNull(message = "Il prezzo giornaliero è obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo non può essere negativo")
    private BigDecimal pricePerDay;
}
