package com.example.qubaatisystem.Service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the OpenAI Chat Completions API.
 * Returns null on any error so the caller can fall back to rule-based analysis.
 * The API key is never logged, returned in a response, or stored outside this class.
 */
@Slf4j
@Service
public class OpenAiService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAiService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /** Returns true only when a non-blank API key is configured. */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Sends a chat request to OpenAI and returns the parsed result.
     * Returns null if the key is missing, the API call fails, or the JSON is malformed.
     */
    @SuppressWarnings("unchecked")
    public AiAnalysisResult analyze(String systemPrompt, String userPrompt) {
        if (!isConfigured()) return null;

        try {
            // Build request body
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user",   "content", userPrompt)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 800);
            requestBody.put("response_format", Map.of("type", "json_object"));

            String requestJson = objectMapper.writeValueAsString(requestBody);

            // HTTP call — body sent and received as plain String to avoid converter issues
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, HttpMethod.POST,
                    new HttpEntity<>(requestJson, headers),
                    String.class
            );

            // Parse outer response → extract content string
            Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            // Parse the JSON content the model produced
            return objectMapper.readValue(content, AiAnalysisResult.class);

        } catch (Exception e) {
            log.warn("OpenAI call failed — using rule-based fallback: {}", e.getMessage());
            return null;
        }
    }

    // ── Inner DTO for the model's JSON output ───────────────────────────────

    @Data
    @NoArgsConstructor
    public static class AiAnalysisResult {
        private String summary;
        private List<String> strengths;
        private List<String> concerns;
        private List<String> recommendedActions;
    }
}
