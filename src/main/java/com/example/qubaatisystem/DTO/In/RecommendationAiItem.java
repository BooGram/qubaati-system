package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.RecommendationPriority;
import com.example.qubaatisystem.Enum.RecommendationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One AI-generated recommendation. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationAiItem {

    private String title;
    private String description;
    private RecommendationType type;
    private RecommendationPriority priority;
}
