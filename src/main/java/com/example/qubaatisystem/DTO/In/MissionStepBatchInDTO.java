package com.example.qubaatisystem.DTO.In;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Body for seeding/authoring a mission's steps (replaces any existing steps for that mission). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepBatchInDTO {

    @Valid
    @NotEmpty(message = "steps must not be empty")
    private List<MissionStepBatchItemInDTO> steps;
}
