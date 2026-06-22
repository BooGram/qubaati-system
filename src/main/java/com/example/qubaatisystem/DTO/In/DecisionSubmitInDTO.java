package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body for submitting a mission decision. responseTimeSeconds is intentionally NOT accepted from the client —
 * the backend computes it from the session's currentStepStartedAt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionSubmitInDTO {

    @NotNull(message = "choiceId is required")
    private Integer choiceId;

    @Size(max = 1000, message = "reason must be at most 1000 characters")
    private String reason;
}
