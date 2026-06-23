package com.campingmanager.bikes.repository;

import com.campingmanager.bikes.entity.BikeRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BikeRentalRepository extends JpaRepository<BikeRental, Long> {

    List<BikeRental> findByOspiteId(Long ospiteId);
    @Query("""
            SELECT COUNT(r) > 0 FROM BikeRental r
            WHERE r.bike.id = :bikeId
              AND r.status <> com.campingmanager.bikes.entity.BikeRentalStatus.CANCELLATO
              AND r.startDate <= :end
              AND r.endDate >= :start
            """)
    boolean existsOverlap(@Param("bikeId") Long bikeId,
                          @Param("start") LocalDate start,
                          @Param("end") LocalDate end);

    long countByStatus(com.campingmanager.bikes.entity.BikeRentalStatus status);
}
