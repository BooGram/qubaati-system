package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionInDTO {

    @NotBlank(message = "title is required")
    @Size(max = 120, message = "title must be at most 120 characters")
    private String title;

    @Size(max = 2000, message = "scenario must be at most 2000 characters")
    private String scenario;

    @NotNull(message = "skillType is required")
    private SkillType skillType;

    @NotNull(message = "difficulty is required")
    private DifficultyLevel difficulty;

    @Positive(message = "estimatedMinutes must be positive")
    private Integer estimatedMinutes;

    @PositiveOrZero(message = "maxScore must be zero or positive")
    private Integer maxScore;

    @NotNull(message = "careerWorldId is required")
    private Integer careerWorldId;
}
