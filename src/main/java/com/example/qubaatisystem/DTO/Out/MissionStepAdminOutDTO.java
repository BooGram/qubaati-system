package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Teacher/admin view of one mission step (authoring/seeding). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepAdminOutDTO {

    private Integer id;
    private Integer stepOrder;
    private String scenario;
    private Boolean finalStep;
    private List<MissionStepChoiceAdminOutDTO> choices;
}
