package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkillInDTO {

    private Integer id;

    @PositiveOrZero(message = "score must be zero or positive")
    private Double score;

    @PositiveOrZero(message = "level must be zero or positive")
    private Integer level;

    @NotNull(message = "studentId is required")
    private Integer studentId;

    @NotNull(message = "skillId is required")
    private Integer skillId;
}
