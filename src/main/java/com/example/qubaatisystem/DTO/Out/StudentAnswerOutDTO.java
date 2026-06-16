package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerOutDTO {

    private Integer id;
    private String answerText;
    private Boolean isCorrect;
    private Integer earnedPoints;
    private java.time.LocalDateTime answeredAt;
    private Integer questionId;
}
