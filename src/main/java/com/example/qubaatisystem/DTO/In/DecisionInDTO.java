package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionInDTO {

    private Integer id;

    @NotBlank(message = "choice is required")
    @Size(max = 255, message = "choice must be at most 255 characters")
    private String choice;

    @Size(max = 1000, message = "reason must be at most 1000 characters")
    private String reason;

    private Boolean isCorrect;

    @PositiveOrZero(message = "responseTimeSeconds must be zero or positive")
    private Double responseTimeSeconds;

    @NotNull(message = "missionSessionId is required")
    private Integer missionSessionId;
}
