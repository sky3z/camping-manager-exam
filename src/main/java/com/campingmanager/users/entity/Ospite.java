package com.campingmanager.users.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// ospite del campeggio: l'account viene creato dallo staff al check-in
@Entity
@Table(name = "ospite")
@DiscriminatorValue("OSPITE")
@Getter
@Setter
@NoArgsConstructor
public class Ospite extends User {

    private String documentNumber;
    private String phone;
    private LocalDate accountValidUntil;

    @Override
    public Role getRole() {
        return Role.OSPITE;
    }

    @Override
    public boolean isEnabled() {
        return accountValidUntil == null || !LocalDate.now().isAfter(accountValidUntil);
    }
}
