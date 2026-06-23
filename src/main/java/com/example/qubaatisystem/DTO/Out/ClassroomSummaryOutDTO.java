package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomSummaryOutDTO {

    // ----- AI analysis fields -----
    private String title;
    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> recommendedActions;
    private String generatedAt;

    // ----- Classroom context -----
    private Integer classroomId;
    private String classroomName;
    private String teacherName;
    private Integer studentCount;
    private Double averagePoints;
    private Double averageCompletedMissions;

    // "openai" when the model generated the text, "fallback" when rule-based logic was used
    private String analysisSource;
}
