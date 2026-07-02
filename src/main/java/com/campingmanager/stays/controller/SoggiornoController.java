package com.campingmanager.stays.controller;

import com.campingmanager.stays.dto.CheckInRequest;
import com.campingmanager.stays.dto.CheckInResponse;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.service.SoggiornoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// la gestione dei soggiorni è dello staff (e admin)
@RestController
@RequestMapping("/api/stays")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class SoggiornoController {

    private final SoggiornoService service;

    @GetMapping
    public ResponseEntity<List<SoggiornoDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoggiornoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<SoggiornoDTO> create(@Valid @RequestBody CreateSoggiornoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SoggiornoDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @PatchMapping("/{id}/checkin")
    public ResponseEntity<CheckInResponse> checkIn(@PathVariable Long id,
                                                   @Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(service.checkIn(id, request));
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<SoggiornoDTO> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkOut(id));
    }
}

