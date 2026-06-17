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
    private Boolean isCorrect;
    private Integer questionId;
}
