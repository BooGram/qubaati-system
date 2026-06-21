package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutOutDTO {

    private String localReference;
    // Publishable key is safe to expose to the frontend for Moyasar.js
    private String publishableKey;
    private String planName;
    private Integer amount;
    private String currency;
    // Pre-built callback URL the frontend should pass to Moyasar
    private String callbackUrl;
}
