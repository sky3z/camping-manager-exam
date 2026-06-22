package com.campingmanager.users.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// amministratore: accesso completo, non ha campi in più
@Entity
@Table(name = "admin")
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
