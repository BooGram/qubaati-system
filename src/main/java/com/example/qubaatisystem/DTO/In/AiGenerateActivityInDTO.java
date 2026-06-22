package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.ActivityType;
import com.example.qubaatisystem.Enum.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input for AI activity generation. Created because the existing {@code ActivityInDTO} is unsuitable:
 * it requires a title and status (both set by the AI flow) and lacks topic / questionCount.
 * teacherId is optional: when provided, the generated Activity is owned by that teacher (Student 1 ownership).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiGenerateActivityInDTO {

    @NotBlank(message = "topic is required")
    @Size(max = 200, message = "topic must be at most 200 characters")
    private String topic;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    private String description;

    @NotNull(message = "type is required")
    private ActivityType type;

    @NotNull(message = "difficulty is required")
    private DifficultyLevel difficulty;

    @Positive(message = "questionCount must be positive")
    private Integer questionCount;

    @PositiveOrZero(message = "maxScore must be zero or positive")
    private Integer maxScore;

    // Optional teacher owner of the generated activity (Student 1 ownership).
    private Integer teacherId;

    // Optional target skill for the generated activity (skillId priority, else skillType). Resolved manually.
    private Integer skillId;
    private com.example.qubaatisystem.Enum.SkillType skillType;
}
