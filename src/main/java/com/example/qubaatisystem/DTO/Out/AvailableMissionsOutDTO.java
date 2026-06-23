package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Missions available to a student in one career world, plus the unlock state (per career world). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableMissionsOutDTO {

    private Integer studentId;
    private Integer careerWorldId;
    private Integer completedDefaultMissions;
    private Boolean personalizedMissionsUnlocked;
    private Integer remainingDefaultMissionsToUnlock;
    private List<AvailableMissionOutDTO> missions;
}
