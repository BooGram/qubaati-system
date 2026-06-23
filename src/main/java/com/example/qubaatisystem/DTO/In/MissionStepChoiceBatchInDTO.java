package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A choice when authoring/seeding mission steps. scoreImpact is internal and never shown to students. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepChoiceBatchInDTO {

    @NotBlank(message = "choice content is required")
    @Size(max = 1000, message = "content must be at most 1000 characters")
    private String content;

    @NotNull(message = "scoreImpact is required")
    private Integer scoreImpact;

    // null => advance to the next step in sequence (currentStep.stepOrder + 1)
    private Integer nextStepOrder;
}
