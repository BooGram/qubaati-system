package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AI insight JSON, mapped onto the Insight entity's fields (numeric scores + summary + recommendation). */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionInsightAiResult {

    private String summary;
    private String recommendation;
    private Integer focusScore;
    private Integer engagementScore;
    private Integer reasoningScore;
    private Integer problemSolvingScore;
    private Integer decisionMakingScore;
}
