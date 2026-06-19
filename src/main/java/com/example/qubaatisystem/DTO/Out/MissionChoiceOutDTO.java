package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionChoiceOutDTO {

    private Integer id;
    private String choiceKey;
    private String text;
    private Integer scoreImpact;
}
