package com.campingmanager.accommodations.dto;

import com.campingmanager.accommodations.entity.Accommodation;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccommodationDTO {

    private Long id;
    private String name;
    private String description;
    private String type;
    private int maxCapacity;
    private BigDecimal pricePerNight;
    private String status;

    public static AccommodationDTO from(Accommodation a) {
        AccommodationDTO dto = new AccommodationDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setDescription(a.getDescription());
        dto.setType(a.getType().name());
        dto.setMaxCapacity(a.getMaxCapacity());
        dto.setPricePerNight(a.getPricePerNight());
        dto.setStatus(a.getStatus().name());
        return dto;
    }
}
