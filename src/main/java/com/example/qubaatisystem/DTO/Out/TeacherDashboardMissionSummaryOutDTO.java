package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Student-3 mission data surfaced (read-only) in the teacher dashboard for the teacher's students. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardMissionSummaryOutDTO {

    private Integer completedMissionSessionsCount;
    private List<String> recentInsightSummaries;
    private List<String> commonWeakSkills;
    private List<String> topRecommendations;
}
