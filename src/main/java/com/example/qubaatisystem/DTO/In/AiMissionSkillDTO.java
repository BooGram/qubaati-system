package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.SkillType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMissionSkillDTO {

    private String name;
    private String description;
    private SkillType skillType;
}
