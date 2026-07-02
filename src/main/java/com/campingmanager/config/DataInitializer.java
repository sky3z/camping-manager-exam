package com.campingmanager.config;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationType;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// all'avvio preparo i dati di base: l'admin e i 10 chalet della struttura
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedChalets();
        seedPiazzole();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        Admin admin = new Admin();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword)); // la salvo cifrata, mai in chiaro
        admin.setName("Admin");
        admin.setSurname("Sistema");
        userRepository.save(admin);
    }

    // creo gli chalet dal numero 1 al 10, saltando quelli che esistono già
    private void seedChalets() {
        for (int i = 1; i <= 10; i++) {
            String name = "Chalet " + i;
            if (accommodationRepository.existsByName(name)) {
                continue;
            }
            Accommodation chalet = new Accommodation();
            chalet.setName(name);
            chalet.setDescription("Chalet in legno numero " + i);
            chalet.setType(AccommodationType.CHALET);
            chalet.setMaxCapacity(4);
            chalet.setPricePerNight(new BigDecimal("80.00"));
            accommodationRepository.save(chalet);
        }
    }

    // stessa cosa per le 10 piazzole in erba
    private void seedPiazzole() {
        for (int i = 1; i <= 10; i++) {
            String name = "Piazzola " + i;
            if (accommodationRepository.existsByName(name)) {
                continue;
            }
            Accommodation piazzola = new Accommodation();
            piazzola.setName(name);
            piazzola.setDescription("Piazzola in erba numero " + i);
            piazzola.setType(AccommodationType.PIAZZOLA);
            piazzola.setMaxCapacity(6);
            piazzola.setPricePerNight(new BigDecimal("25.00"));
            accommodationRepository.save(piazzola);
        }
    }
}
