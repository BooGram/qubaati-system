package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightOutDTO {

    private Integer id;
    private Integer focusScore;
    private Integer engagementScore;
    private Integer reasoningScore;
    private Integer problemSolvingScore;
    private Integer decisionMakingScore;
    private String summary;
    private String recommendation;
    private Integer missionSessionId;
}
