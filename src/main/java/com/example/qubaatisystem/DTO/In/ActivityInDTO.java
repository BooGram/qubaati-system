package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Enum.ActivityType;
import com.example.qubaatisystem.Enum.DifficultyLevel;
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
public class ActivityInDTO {

    @NotBlank(message = "title is required")
    @Size(max = 150, message = "title must be at most 150 characters")
    private String title;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    private String description;

    @NotNull(message = "type is required")
    private ActivityType type;

    @NotNull(message = "status is required")
    private ActivityStatus status;

    @NotNull(message = "difficulty is required")
    private DifficultyLevel difficulty;

    @PositiveOrZero(message = "maxScore must be zero or positive")
    private Integer maxScore;

    // Optional teacher owner (Student 1 ownership). Null keeps the activity unowned (backward compatible).
    private Integer teacherId;

    // Optional target skill. skillId has priority; else resolve an existing Skill of skillType; else null
    // (analytics then fall back to a PROBLEM_SOLVING skill). Resolved manually in ActivityService.
    private Integer skillId;
    private com.example.qubaatisystem.Enum.SkillType skillType;
}
