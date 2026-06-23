package com.campingmanager.bikes.controller;

import com.campingmanager.bikes.dto.BikeDTO;
import com.campingmanager.bikes.dto.CreateBikeRequest;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.entity.BikeType;
import com.campingmanager.bikes.service.BikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService service;

    // tutti gli autenticati vedono le bici (anche l'ospite, che poi le noleggia)
    @GetMapping
    public ResponseEntity<List<BikeDTO>> getAll(@RequestParam(required = false) BikeType type,
                                                @RequestParam(required = false) BikeStatus status) {
        return ResponseEntity.ok(service.getAll(type, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BikeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<BikeDTO> create(@Valid @RequestBody CreateBikeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<BikeDTO> update(@PathVariable Long id, @Valid @RequestBody CreateBikeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
