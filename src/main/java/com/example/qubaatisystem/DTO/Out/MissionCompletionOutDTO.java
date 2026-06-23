package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.MissionSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Summary returned by complete-mission-session, including the internally generated insight + updates. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionCompletionOutDTO {

    private Integer sessionId;
    private Integer missionId;
    private Integer studentId;
    private MissionSessionStatus status;
    private Integer score;
    private LocalDateTime completedAt;
    private InsightOutDTO insight;
    private List<SkillUpdateOutDTO> updatedSkills;
    private Boolean personalizedMissionsUnlocked;
    private Boolean newGeneratedMissionCreated;
}
