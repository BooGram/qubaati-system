package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * One mission step shown to the student: the step's order + scenario + its student-safe choices (no scoring).
 * For legacy single-step missions stepOrder is 1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepOutDTO {

    private Integer stepOrder;
    private String scenario;
    private List<StudentMissionChoiceOutDTO> choices;
}
