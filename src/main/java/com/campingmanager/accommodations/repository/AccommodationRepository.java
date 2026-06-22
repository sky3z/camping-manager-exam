package com.campingmanager.accommodations.repository;

import com.campingmanager.accommodations.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
