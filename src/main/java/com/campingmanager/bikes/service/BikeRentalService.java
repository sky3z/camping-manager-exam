package com.campingmanager.bikes.service;

import com.campingmanager.bikes.dto.BikeRentalDTO;
import com.campingmanager.bikes.dto.CreateRentalRequest;
import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeRental;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BikeRentalService {

    private final BikeRentalRepository rentalRepository;
    private final BikeRepository bikeRepository;

    // l'ospite noleggia: nasce in PENDING_PAYMENT, lo pagherà con Stripe
    public BikeRentalDTO createRental(Ospite ospite, CreateRentalRequest request) {
        Bike bike = bikeRepository.findById(request.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bici non trovata con id: " + request.getBikeId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("La data di fine non può precedere quella di inizio");
        }
        if (rentalRepository.existsOverlap(bike.getId(), request.getStartDate(), request.getEndDate())) {
            throw new ConflictException("La bici è già noleggiata in quel periodo");
        }

        // giorni inclusivi: dal 1 al 3 sono 3 giorni
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        BikeRental rental = new BikeRental();
        rental.setOspite(ospite);
        rental.setBike(bike);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalPrice(bike.getPricePerDay().multiply(BigDecimal.valueOf(days)));
        rental.setStatus(BikeRentalStatus.PENDING_PAYMENT);

        return BikeRentalDTO.from(rentalRepository.save(rental));
    }

    @Transactional(readOnly = true)
    public List<BikeRentalDTO> getRentals(User currentUser) {
        // l'ospite vede solo i suoi noleggi, staff/admin li vedono tutti
        List<BikeRental> rentals = (currentUser instanceof Ospite ospite)
                ? rentalRepository.findByOspiteId(ospite.getId())
                : rentalRepository.findAll();
        return rentals.stream().map(BikeRentalDTO::from).toList();
    }

    // restituzione: chiudo il noleggio e rimetto la bici disponibile
    public BikeRentalDTO returnRental(Long id) {
        BikeRental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato con id: " + id));
        rental.setStatus(BikeRentalStatus.COMPLETATO);

        Bike bike = rental.getBike();
        bike.setStatus(BikeStatus.DISPONIBILE);
        bikeRepository.save(bike);

        return BikeRentalDTO.from(rentalRepository.save(rental));
    }
}
