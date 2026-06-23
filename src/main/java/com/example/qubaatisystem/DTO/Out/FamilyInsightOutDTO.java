package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInsightOutDTO {

    // ----- AI analysis fields -----
    private String title;
    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> recommendedActions;
    private String generatedAt;
    // "openai" when the model generated the text, "fallback" when rule-based logic was used
    private String analysisSource;

    // ----- Family context (always from backend, never from the model) -----
    private Integer parentId;
    private String parentName;
    private Integer childrenCount;
}
