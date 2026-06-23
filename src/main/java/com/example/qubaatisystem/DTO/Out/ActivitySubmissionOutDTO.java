package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySubmissionOutDTO {

    private Integer id;
    private java.time.LocalDateTime startedAt;
    private java.time.LocalDateTime submittedAt;
    private Integer score;
    private ActivitySubmissionStatus status;
    private String aiFeedback;
    // "AI" when feedback came from Spring AI; "SYSTEM" when it is the deterministic score summary (no AI provider).
    private String feedbackSource;
    private String teacherFeedback;
    private Integer activityAssignmentId;
    private Integer activityId;
    private String activityTitle;
    private Integer activityMaxScore;
    private Integer studentId;
    private String studentName;
}
