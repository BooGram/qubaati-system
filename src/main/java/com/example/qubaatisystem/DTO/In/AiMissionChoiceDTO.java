package com.example.qubaatisystem.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMissionChoiceDTO {

    private String key;
    private String text;
    private Integer scoreImpact;
}
