package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** AI (or rule-based fallback) insight for the teacher dashboard. Same shape as the other Student-1 analyses. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardInsightOutDTO {

    private String title;
    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> recommendedActions;
    private String generatedAt;
    private String source; // "openai" or "fallback"

    private Integer teacherId;
    private String teacherName;
}
