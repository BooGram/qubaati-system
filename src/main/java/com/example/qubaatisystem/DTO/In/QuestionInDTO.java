package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.QuestionType;
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
public class QuestionInDTO {

    @NotBlank(message = "content is required")
    @Size(max = 2000, message = "content must be at most 2000 characters")
    private String content;

    @NotNull(message = "type is required")
    private QuestionType type;

    @PositiveOrZero(message = "points must be zero or positive")
    private Integer points;

    @NotNull(message = "difficulty is required")
    private DifficultyLevel difficulty;

    @Size(max = 1000, message = "correctAnswer must be at most 1000 characters")
    private String correctAnswer;

    @NotNull(message = "activityId is required")
    private Integer activityId;
}
