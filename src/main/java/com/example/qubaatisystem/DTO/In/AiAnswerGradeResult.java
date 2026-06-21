package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured result of AI-assisted grading for a text (SHORT_ANSWER / OPEN_ENDED) answer.
 * status is the AI's classification (CORRECT / INCORRECT / PARTIAL) as plain text; the service reconciles it
 * with the clamped earnedPoints. Never exposed to students.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnswerGradeResult {

    private Integer earnedPoints;
    private String status;
    private String feedback;
}
