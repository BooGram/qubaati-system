package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkillOutDTO {

    private Integer id;
    private Double score;
    private Integer level;
    private java.time.LocalDateTime lastUpdated;
    private Integer studentId;
    private Integer skillId;
    private String skillName;
}
