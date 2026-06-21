package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMissionDTO {

    private String title;
    private String description;
    private String scenario;
    private List<AiMissionChoiceDTO> choices;      // legacy single-step fallback (old format)
    private List<AiMissionStepDTO> steps;          // multi-step format
    private AiMissionSkillDTO skill;
    private DifficultyLevel difficulty;
    private Integer estimatedMinutes;
    private Integer maxScore;
}
