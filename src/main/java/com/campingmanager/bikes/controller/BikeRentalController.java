package com.campingmanager.bikes.controller;

import com.campingmanager.bikes.dto.BikeRentalDTO;
import com.campingmanager.bikes.dto.CreateRentalRequest;
import com.campingmanager.bikes.service.BikeRentalService;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikes/rentals")
@RequiredArgsConstructor
public class BikeRentalController {

    private final BikeRentalService service;

    // solo l'ospite noleggia
    @PostMapping
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<BikeRentalDTO> create(@AuthenticationPrincipal Ospite ospite,
                                                @Valid @RequestBody CreateRentalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRental(ospite, request));
    }

    @GetMapping
    public ResponseEntity<List<BikeRentalDTO>> getRentals(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getRentals(currentUser));
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<BikeRentalDTO> returnRental(@PathVariable Long id) {
        return ResponseEntity.ok(service.returnRental(id));
    }
}
