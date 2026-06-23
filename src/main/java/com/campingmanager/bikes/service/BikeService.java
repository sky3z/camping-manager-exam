package com.campingmanager.bikes.service;

import com.campingmanager.bikes.dto.BikeDTO;
import com.campingmanager.bikes.dto.CreateBikeRequest;
import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.entity.BikeType;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository repository;

    public List<BikeDTO> getAll(BikeType type, BikeStatus status) {
        return repository.findAll().stream()
                .filter(b -> type == null || b.getType() == type)
                .filter(b -> status == null || b.getStatus() == status)
                .map(BikeDTO::from)
                .toList();
    }

    public BikeDTO getById(Long id) {
        return BikeDTO.from(findOrThrow(id));
    }

    public BikeDTO create(CreateBikeRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new ConflictException("Esiste già una bici con codice " + request.getCode());
        }
        Bike bike = new Bike();
        bike.setCode(request.getCode());
        bike.setModel(request.getModel());
        bike.setType(request.getType());
        bike.setPricePerDay(request.getPricePerDay());
        return BikeDTO.from(repository.save(bike));
    }

    public BikeDTO update(Long id, CreateBikeRequest request) {
        Bike bike = findOrThrow(id);
        bike.setModel(request.getModel());
        bike.setType(request.getType());
        bike.setPricePerDay(request.getPricePerDay());
        return BikeDTO.from(repository.save(bike));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Bici non trovata con id: " + id);
        }
        repository.deleteById(id);
    }

    private Bike findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bici non trovata con id: " + id));
    }
}
