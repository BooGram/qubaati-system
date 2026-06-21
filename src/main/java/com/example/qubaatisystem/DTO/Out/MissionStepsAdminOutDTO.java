package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.MissionSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Teacher/admin view of a mission with its full ordered steps (returned by the steps batch/get endpoints). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStepsAdminOutDTO {

    private Integer missionId;
    private String title;
    private MissionSource source;
    private List<MissionStepAdminOutDTO> steps;
}
