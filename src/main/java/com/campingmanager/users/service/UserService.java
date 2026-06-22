package com.campingmanager.users.service;

import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.users.dto.ChangePasswordRequest;
import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    // restituisco i dati dell'utente che ha fatto la richiesta
    public UserResponseDTO getMe(User currentUser) {
        return UserResponseDTO.from(currentUser);
    }

    // carico l'immagine su Cloudinary, salvo l'URL nel profilo e aggiorno l'utente
    public UserResponseDTO updateAvatar(User currentUser, MultipartFile file) {
        String imageUrl = cloudinaryService.upload(file);
        currentUser.setProfileImage(imageUrl);
        userRepository.save(currentUser);
        return UserResponseDTO.from(currentUser);
    }

    // cambio password: prima controllo che la vecchia sia giusta
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BadRequestException("La password attuale non è corretta");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}
