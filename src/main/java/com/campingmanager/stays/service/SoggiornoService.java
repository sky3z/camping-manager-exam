package com.campingmanager.stays.service;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SoggiornoService {

    private final SoggiornoRepository soggiornoRepository;
    private final AccommodationRepository accommodationRepository;

    public SoggiornoDTO create(CreateSoggiornoRequest request) {
        Accommodation accommodation = accommodationRepository.findById(request.getAccommodationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alloggio non trovato con id: " + request.getAccommodationId()));

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new BadRequestException("Il check-out deve essere successivo al check-in");
        }

        if (soggiornoRepository.existsOverlap(accommodation.getId(),
                request.getCheckInDate(), request.getCheckOutDate())) {
            throw new ConflictException("L'alloggio è già prenotato in quelle date");
        }

        Soggiorno soggiorno = new Soggiorno();
        soggiorno.setAccommodation(accommodation);
        soggiorno.setGuestName(request.getGuestName());
        soggiorno.setGuestEmail(request.getGuestEmail());
        soggiorno.setNumGuests(request.getNumGuests());
        soggiorno.setCheckInDate(request.getCheckInDate());
        soggiorno.setCheckOutDate(request.getCheckOutDate());
        soggiorno.setTotalPrice(accommodation.getPricePerNight().multiply(BigDecimal.valueOf(nights)));

        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    public List<SoggiornoDTO> getAll() {
        return soggiornoRepository.findAll().stream().map(SoggiornoDTO::from).toList();
    }

    public SoggiornoDTO getById(Long id) {
        return SoggiornoDTO.from(findOrThrow(id));
    }

    public SoggiornoDTO cancel(Long id) {
        Soggiorno soggiorno = findOrThrow(id);
        soggiorno.setStatus(SoggiornoStatus.CANCELLATO);
        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    private Soggiorno findOrThrow(Long id) {
        return soggiornoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soggiorno non trovato con id: " + id));
    }
}
