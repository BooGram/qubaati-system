package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

/**
 * Thin HTTP client for Moyasar REST API.
 * The secret key is used only to build the Authorization header and is never logged or returned.
 */
@Slf4j
@Service
public class MoyasarService {

    @Value("${moyasar.secret-key:}")
    private String secretKey;

    @Value("${moyasar.api-base-url:https://api.moyasar.com/v1}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MoyasarService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /** Returns true only when a non-blank secret key is configured. */
    public boolean isConfigured() {
        return secretKey != null && !secretKey.isBlank();
    }

    /**
     * Fetches payment details from Moyasar by Moyasar payment ID.
     * Returns null if the key is missing, the call fails, or the response is malformed.
     * Caller must treat null as unverifiable and reject the callback.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchPayment(String moyasarPaymentId) {
        if (!isConfigured()) return null;
        try {
            String url = apiBaseUrl + "/payments/" + moyasarPaymentId;
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(buildAuthHeaders()),
                    String.class
            );
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            log.warn("Moyasar fetchPayment failed for id={}: {}", moyasarPaymentId, e.getMessage());
            return null;
        }
    }

    /** Throws ApiException when Moyasar keys are not configured (used during checkout). */
    public void requireConfigured() {
        if (!isConfigured()) {
            throw new ApiException("Payment gateway is not configured. Please contact support.");
        }
    }

    // ---------- helpers ----------

    private HttpHeaders buildAuthHeaders() {
        // Moyasar uses HTTP Basic auth: base64(secretKey:) — the password is intentionally empty
        String credentials = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        return headers;
    }
}
