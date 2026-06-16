package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionOutDTO {

    private Integer id;
    private String choice;
    private String reason;
    private Boolean isCorrect;
    private Double responseTimeSeconds;
    private Integer missionSessionId;
}
