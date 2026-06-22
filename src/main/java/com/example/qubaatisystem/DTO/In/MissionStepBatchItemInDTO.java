package com.example.qubaatisystem.DTO.In;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** One step when authoring/seeding a multi-step mission. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepBatchItemInDTO {

    @NotNull(message = "stepOrder is required")
    private Integer stepOrder;

    private String scenario;

    @Valid
    @NotEmpty(message = "each step must have at least one choice")
    private List<MissionStepChoiceBatchInDTO> choices;
}
