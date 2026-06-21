package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Teacher/admin view of a step choice (authoring): includes the internal scoreImpact + branch target. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepChoiceAdminOutDTO {

    private Integer id;
    private String choiceKey;
    private String content;
    private Integer scoreImpact;
    private Integer nextStepOrder;
}
