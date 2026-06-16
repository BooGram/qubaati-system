package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionOutDTO {

    private Integer id;
    private String title;
    private String scenario;
    private SkillType skillType;
    private DifficultyLevel difficulty;
    private Integer estimatedMinutes;
    private Integer maxScore;
    private Integer careerWorldId;
    private String careerWorldTitle;
}
