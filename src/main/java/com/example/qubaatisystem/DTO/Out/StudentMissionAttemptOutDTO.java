package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.MissionSessionStatus;
import com.example.qubaatisystem.Enum.MissionSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Student-safe mission attempt returned by start/current. Never exposes choice scoring or outcomes. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMissionAttemptOutDTO {

    private Integer sessionId;
    private Integer missionId;
    private Integer studentId;
    private Integer careerWorldId;
    private String title;
    private String description;
    private MissionSource source;
    private MissionSessionStatus status;
    private LocalDateTime startedAt;
    private MissionStepOutDTO currentStep;
}
