package com.campingmanager.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponse {
    private String checkoutUrl;
    private String sessionId;
}
