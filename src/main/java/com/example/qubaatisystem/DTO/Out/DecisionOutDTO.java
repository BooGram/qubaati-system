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
    private Double responseTimeSeconds;
    private Integer missionSessionId;
    // isCorrect deliberately omitted: it is a hidden correctness flag and this generic decision DTO is not
    // gated. The mission flow never sets Decision.isCorrect anyway (scoring uses the internal scoreImpact).
}
