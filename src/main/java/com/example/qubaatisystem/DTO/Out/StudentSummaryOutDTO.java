package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryOutDTO {

    // ----- AI analysis fields -----
    private String title;
    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> recommendedActions;
    private String generatedAt;

    // ----- Student context -----
    private Integer parentId;
    private Integer studentId;
    private String studentName;
    private String grade;
    private Integer age;
    private Integer totalPoints;
    private Integer completedMissionsCount;

    // "openai" when the model generated the text, "fallback" when rule-based logic was used
    private String analysisSource;
}
