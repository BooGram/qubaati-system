package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A single skill change produced by mission completion. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillUpdateOutDTO {

    private String skillName;
    private Double previousScore;
    private Double newScore;
}
