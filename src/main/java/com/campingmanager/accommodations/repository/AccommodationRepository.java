package com.campingmanager.accommodations.repository;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.entity.AccommodationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByType(AccommodationType type);

    boolean existsByName(String name);

    // alloggi liberi nell'intervallo: non in manutenzione e senza soggiorni (non cancellati)
    // che si sovrappongono alle date richieste (subquery NOT IN)
    @Query("""
            SELECT a FROM Accommodation a
            WHERE a.status <> com.campingmanager.accommodations.entity.AccommodationStatus.MANUTENZIONE
              AND a.id NOT IN (
                  SELECT s.accommodation.id FROM Soggiorno s
                  WHERE s.status <> com.campingmanager.stays.entity.SoggiornoStatus.CANCELLATO
                    AND s.checkInDate < :checkOut
                    AND s.checkOutDate > :checkIn
              )
            ORDER BY a.pricePerNight ASC
            """)
    List<Accommodation> findAvailable(@Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut);

    long countByStatus(AccommodationStatus status);
}
