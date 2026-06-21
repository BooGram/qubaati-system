package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOutDTO {

    private Integer id;
    private String content;
    private QuestionType type;
    private Integer points;
    private DifficultyLevel difficulty;
    private Integer activityId;
    // correctAnswer is intentionally NOT exposed here (student-safe generic DTO). Teachers see it via
    // ActivityDetailsOutDTO/QuestionDetailsOutDTO (GET /activities/{id}/details) and the teacher submission details.
}
