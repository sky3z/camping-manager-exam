package com.campingmanager.stays.service;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.stays.dto.CheckInRequest;
import com.campingmanager.stays.dto.CheckInResponse;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SoggiornoService {

    private final SoggiornoRepository soggiornoRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    public List<SoggiornoDTO> getAll() {
        return soggiornoRepository.findAll().stream().map(SoggiornoDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public SoggiornoDTO getById(Long id) {
        return SoggiornoDTO.from(findOrThrow(id));
    }

    public SoggiornoDTO cancel(Long id) {
        Soggiorno soggiorno = findOrThrow(id);
        soggiorno.setStatus(SoggiornoStatus.CANCELLATO);
        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    // check-in: creo l'account ospite (valido fino al check-out), segno il soggiorno e occupo l'alloggio
    public CheckInResponse checkIn(Long id, CheckInRequest request) {
        Soggiorno soggiorno = findOrThrow(id);
        if (soggiorno.getStatus() != SoggiornoStatus.PRENOTATO) {
            throw new BadRequestException("Il check-in è possibile solo per un soggiorno PRENOTATO");
        }
        if (userRepository.existsByEmail(soggiorno.getGuestEmail())) {
            throw new ConflictException("Esiste già un account con email " + soggiorno.getGuestEmail());
        }

        String tempPassword = generatePassword();

        Ospite ospite = new Ospite();
        ospite.setEmail(soggiorno.getGuestEmail());
        ospite.setPassword(passwordEncoder.encode(tempPassword));
        // dal nome completo ricavo nome e cognome
        String[] parts = soggiorno.getGuestName().trim().split("\\s+", 2);
        ospite.setName(parts[0]);
        ospite.setSurname(parts.length > 1 ? parts[1] : "-");
        ospite.setDocumentNumber(request.getDocumentNumber());
        ospite.setPhone(request.getPhone());
        ospite.setAccountValidUntil(soggiorno.getCheckOutDate());
        Ospite savedOspite = userRepository.save(ospite);

        soggiorno.setOspiteAccount(savedOspite);
        soggiorno.setStatus(SoggiornoStatus.CHECKED_IN);

        Accommodation accommodation = soggiorno.getAccommodation();
        accommodation.setStatus(AccommodationStatus.OCCUPATA);
        accommodationRepository.save(accommodation);

        Soggiorno saved = soggiornoRepository.save(soggiorno);
        return new CheckInResponse(SoggiornoDTO.from(saved), savedOspite.getEmail(), tempPassword);
    }

    // check-out: chiudo il soggiorno e libero l'alloggio
    public SoggiornoDTO checkOut(Long id) {
        Soggiorno soggiorno = findOrThrow(id);
        if (soggiorno.getStatus() != SoggiornoStatus.CHECKED_IN) {
            throw new BadRequestException("Il check-out è possibile solo per un soggiorno CHECKED_IN");
        }
        soggiorno.setStatus(SoggiornoStatus.CHECKED_OUT);

        Accommodation accommodation = soggiorno.getAccommodation();
        accommodation.setStatus(AccommodationStatus.DISPONIBILE);
        accommodationRepository.save(accommodation);

        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    private Soggiorno findOrThrow(Long id) {
        return soggiornoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soggiorno non trovato con id: " + id));
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
