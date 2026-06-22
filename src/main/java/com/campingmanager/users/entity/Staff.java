package com.campingmanager.users.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// personale della struttura: ha un suo campo (il reparto in cui lavora)
@Entity
@Table(name = "staff")
@DiscriminatorValue("STAFF")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends User {

    private String department;

    @Override
    public Role getRole() {
        return Role.STAFF;
    }
}
