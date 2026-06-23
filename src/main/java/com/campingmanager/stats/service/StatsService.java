package com.campingmanager.stats.service;

import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.stats.dto.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final AccommodationRepository accommodationRepository;
    private final BikeRentalRepository bikeRentalRepository;

    public StatsResponse getStats() {
        StatsResponse stats = new StatsResponse();

        // occupazione alloggi
        StatsResponse.OccupazioneStats occ = new StatsResponse.OccupazioneStats();
        long totale = accommodationRepository.count();
        long occupati = accommodationRepository.countByStatus(AccommodationStatus.OCCUPATA);
        long manutenzione = accommodationRepository.countByStatus(AccommodationStatus.MANUTENZIONE);
        occ.setTotale(totale);
        occ.setOccupati(occupati);
        occ.setInManutenzione(manutenzione);
        occ.setDisponibili(totale - occupati - manutenzione);
        occ.setPercentualeOccupazione(totale > 0 ? (double) occupati / totale * 100 : 0);
        stats.setOccupazione(occ);

        // noleggi bici suddivisi per stato
        stats.setNoleggiPerStato(Map.of(
                "PENDING_PAYMENT", bikeRentalRepository.countByStatus(BikeRentalStatus.PENDING_PAYMENT),
                "ATTIVO",          bikeRentalRepository.countByStatus(BikeRentalStatus.ATTIVO),
                "COMPLETATO",      bikeRentalRepository.countByStatus(BikeRentalStatus.COMPLETATO),
                "CANCELLATO",      bikeRentalRepository.countByStatus(BikeRentalStatus.CANCELLATO)
        ));

        return stats;
    }
}
