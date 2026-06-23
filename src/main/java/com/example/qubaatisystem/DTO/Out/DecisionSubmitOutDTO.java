package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Result of submitting a decision. responseTimeSeconds is backend-calculated, never client-supplied. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionSubmitOutDTO {

    private Integer decisionId;
    private Integer sessionId;
    private Integer choiceId;
    private Double responseTimeSeconds;
    private LocalDateTime submittedAt;
    private Boolean missionCompleteReady;
    private MissionStepOutDTO nextStep;
}
