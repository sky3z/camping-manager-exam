package com.campingmanager.stays.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// dopo il check-in restituisco il soggiorno aggiornato e le credenziali generate per l'ospite
@Data
@AllArgsConstructor
public class CheckInResponse {
    private SoggiornoDTO soggiorno;
    private String ospiteEmail;
    private String temporaryPassword;
}
