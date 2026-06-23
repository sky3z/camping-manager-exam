package com.campingmanager.bikes.repository;

import com.campingmanager.bikes.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    boolean existsByCode(String code);
}
