package com.campingmanager.users.service;

import com.campingmanager.exceptions.BadRequestException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// carico le immagini su Cloudinary e mi faccio restituire l'URL pubblico
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Il file caricato è vuoto");
        }
        try {
            // carico i byte del file dentro una cartella "camping/avatars"
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "camping/avatars"));
            // secure_url è il link https dell'immagine, che salvo nel profilo
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new IllegalStateException("Errore durante l'upload su Cloudinary", e);
        }
    }
}
