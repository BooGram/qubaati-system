package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Parent-facing combined learning profile for one child: skills, learning style, recent activity performance,
 * recent mission insight, recommendations, and activity/mission completion. Never exposes correct answers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildLearningProfileOutDTO {

    private Integer studentId;
    private String fullName;
    private String grade;
    private Integer age;
    private Integer totalPoints;
    private Integer completedMissionsCount;

    // Learning style.
    private String learningStylePrimary;
    private String learningStyleSecondary;
    private Double learningStyleConfidence;

    // Skills.
    private List<SkillRow> skills;
    private List<String> weakSkills;

    // Activity completion / performance (Student 2 data).
    private Integer activitiesTotal;
    private Integer activitiesGraded;
    private Integer activitiesInProgress;
    private Integer activitiesReturned;
    private Double averageActivityScore;

    // Mission completion (Student 3 data).
    private Integer completedMissionSessionsCount;
    private Integer activeMissionSessionsCount;
    private String latestMissionInsightSummary;
    private String latestMissionInsightRecommendation;

    // Recommendations.
    private List<String> topRecommendations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillRow {
        private String skillName;
        private String skillType;
        private Double score;
        private Integer level;
    }
}
