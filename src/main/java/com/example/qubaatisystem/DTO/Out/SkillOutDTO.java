package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillOutDTO {

    private Integer id;
    private String name;
    private String description;
    private SkillType skillType;
}
