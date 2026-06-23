package com.campingmanager.accommodations.controller;

import com.campingmanager.accommodations.dto.AccommodationDTO;
import com.campingmanager.accommodations.dto.CreateAccommodationRequest;
import com.campingmanager.accommodations.entity.AccommodationType;
import com.campingmanager.accommodations.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService service;

    // lettura aperta a tutti gli utenti autenticati, con filtro opzionale per tipo
    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAll(@RequestParam(required = false) AccommodationType type) {
        return ResponseEntity.ok(service.getAll(type));
    }

    // alloggi liberi in un intervallo di date
    @GetMapping("/available")
    public ResponseEntity<List<AccommodationDTO>> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) AccommodationType type) {
        return ResponseEntity.ok(service.getAvailable(checkIn, checkOut, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // creazione/modifica/eliminazione solo per l'admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> create(@Valid @RequestBody CreateAccommodationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody CreateAccommodationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
