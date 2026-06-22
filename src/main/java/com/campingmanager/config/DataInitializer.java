package com.campingmanager.config;

import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// all'avvio creo l'admin iniziale se non c'è ancora, così posso subito loggarmi
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
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
}
