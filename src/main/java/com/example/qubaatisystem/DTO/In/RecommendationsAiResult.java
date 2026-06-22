package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Wrapper for the AI recommendations JSON ({"recommendations": [...]}). */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationsAiResult {

    private List<RecommendationAiItem> recommendations;
}
