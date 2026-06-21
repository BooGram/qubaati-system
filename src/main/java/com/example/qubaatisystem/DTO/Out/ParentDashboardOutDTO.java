package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDashboardOutDTO {

    private Integer parentId;
    private String parentName;
    private Integer registeredChildrenCount;
    private Integer totalCompletedMissionsCount;
    private List<ChildCard> children;

    /**
     * Lean child representation for the dashboard card.
     * Contains only display-safe fields — no account credentials, contact info, or parent back-references.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildCard {
        private Integer studentId;
        private String fullName;
        private String grade;
        private Integer age;
        private Integer totalPoints;
        private Integer completedMissionsCount;
        private Integer classroomId;   // null when not enrolled
        private String classroomName;  // null when not enrolled

        // Student 2 activity progress + Student 3 mission progress (read-only).
        private Integer gradedActivitiesCount;
        private Double averageActivityScore;
        private Integer completedMissionSessionsCount;
        private String latestInsightSummary;
    }
}
