package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightInDTO {

    @PositiveOrZero(message = "focusScore must be zero or positive")
    @Max(value = 100, message = "focusScore must be at most 100")
    private Integer focusScore;

    @PositiveOrZero(message = "engagementScore must be zero or positive")
    @Max(value = 100, message = "engagementScore must be at most 100")
    private Integer engagementScore;

    @PositiveOrZero(message = "reasoningScore must be zero or positive")
    @Max(value = 100, message = "reasoningScore must be at most 100")
    private Integer reasoningScore;

    @PositiveOrZero(message = "problemSolvingScore must be zero or positive")
    @Max(value = 100, message = "problemSolvingScore must be at most 100")
    private Integer problemSolvingScore;

    @PositiveOrZero(message = "decisionMakingScore must be zero or positive")
    @Max(value = 100, message = "decisionMakingScore must be at most 100")
    private Integer decisionMakingScore;

    @Size(max = 2000, message = "summary must be at most 2000 characters")
    private String summary;

    @Size(max = 2000, message = "recommendation must be at most 2000 characters")
    private String recommendation;

    @NotNull(message = "missionSessionId is required")
    private Integer missionSessionId;
}
