package com.campingmanager.accommodations.service;

import com.campingmanager.accommodations.dto.AccommodationDTO;
import com.campingmanager.accommodations.dto.CreateAccommodationRequest;
import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationType;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository repository;

    // se passo un tipo filtro, altrimenti restituisco tutti
    public List<AccommodationDTO> getAll(AccommodationType type) {
        List<Accommodation> list = (type == null) ? repository.findAll() : repository.findByType(type);
        return list.stream().map(AccommodationDTO::from).toList();
    }

    public AccommodationDTO getById(Long id) {
        return AccommodationDTO.from(findOrThrow(id));
    }

    public AccommodationDTO create(CreateAccommodationRequest request) {
        Accommodation a = new Accommodation();
        applyData(a, request);
        return AccommodationDTO.from(repository.save(a));
    }

    public AccommodationDTO update(Long id, CreateAccommodationRequest request) {
        Accommodation a = findOrThrow(id);
        applyData(a, request);
        return AccommodationDTO.from(repository.save(a));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Alloggio non trovato con id: " + id);
        }
        repository.deleteById(id);
    }

    private Accommodation findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato con id: " + id));
    }

    // copio i dati della richiesta sull'entità (lo riuso in create e update)
    private void applyData(Accommodation a, CreateAccommodationRequest request) {
        a.setName(request.getName());
        a.setDescription(request.getDescription());
        a.setType(request.getType());
        a.setMaxCapacity(request.getMaxCapacity());
        a.setPricePerNight(request.getPricePerNight());
    }
}
