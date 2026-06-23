package com.campingmanager.bikes.entity;

// PENDING_PAYMENT finché non viene pagato, poi ATTIVO; COMPLETATO alla restituzione
public enum BikeRentalStatus {
    PENDING_PAYMENT, ATTIVO, COMPLETATO, CANCELLATO
}
