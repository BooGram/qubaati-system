package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionOutDTO {

    private Integer id;
    private String content;
    private Integer questionId;
    // isCorrect is intentionally NOT exposed here (student-safe generic DTO). Teachers see it via
    // OptionDetailsOutDTO (GET /activities/{id}/details) and the teacher submission details.
}
