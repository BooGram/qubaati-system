package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One AI-generated choice within a mission step. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMissionStepChoiceDTO {

    private String content;
    private Integer scoreImpact;
    private Integer nextStepOrder;
}
