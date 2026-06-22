package com.campingmanager.stays.repository;

import com.campingmanager.stays.entity.Soggiorno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SoggiornoRepository extends JpaRepository<Soggiorno, Long> {

    // true se l'alloggio ha già una prenotazione (non cancellata) che si accavalla con le date richieste.
    // due intervalli si sovrappongono se inizio1 < fine2 e fine1 > inizio2
    @Query("""
            SELECT COUNT(s) > 0 FROM Soggiorno s
            WHERE s.accommodation.id = :accommodationId
              AND s.status <> com.campingmanager.stays.entity.SoggiornoStatus.CANCELLATO
              AND s.checkInDate < :checkOut
              AND s.checkOutDate > :checkIn
            """)
    boolean existsOverlap(@Param("accommodationId") Long accommodationId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut);
}
