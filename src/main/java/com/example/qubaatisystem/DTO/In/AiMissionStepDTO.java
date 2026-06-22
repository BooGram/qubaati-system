package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** One AI-generated mission step (ordered scenario + branching choices). */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMissionStepDTO {

    private Integer stepOrder;
    private String scenario;
    private List<AiMissionStepChoiceDTO> choices;
}
