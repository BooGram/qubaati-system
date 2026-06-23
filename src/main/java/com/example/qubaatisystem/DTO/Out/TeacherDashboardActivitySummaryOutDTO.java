package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Student-2 activity data surfaced (read-only) in the teacher dashboard. No correct answers are exposed. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardActivitySummaryOutDTO {

    private Integer ownedActivitiesCount;
    private Integer draftCount;
    private Integer pendingReviewCount;
    private Integer approvedCount;
    private Integer rejectedCount;
    private Integer archivedCount;

    private Integer assignedActivitiesCount;
    private Integer submissionsCount;
    private Integer pendingGradingCount;
    private Integer gradedSubmissionsCount;
    private Integer returnedSubmissionsCount;
    private Double averageActivityScore;

    private Integer dueSoonAssignmentsCount;
    private Integer overdueAssignmentsCount;
}
