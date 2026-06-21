package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.AnswerStatus;
import com.example.qubaatisystem.Enum.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Teacher-only per-answer grading row. Includes correctAnswer because this is exposed ONLY through the
 * teacher submission-details endpoint (never through any student-facing view).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAnswerRowOutDTO {

    private Integer answerId;
    private Integer questionId;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private String answerText;
    private Integer earnedPoints;
    private AnswerStatus status;
    private String feedback;
    private String correctAnswer;
}
