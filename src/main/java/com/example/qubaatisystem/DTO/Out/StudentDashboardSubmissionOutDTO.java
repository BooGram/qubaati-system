package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** One submission row in the student activity dashboard (returned work / recent results). No correct answers. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardSubmissionOutDTO {

    private Integer submissionId;
    private Integer activityId;
    private String activityTitle;
    private ActivitySubmissionStatus status;
    private Integer score;
    private Integer activityMaxScore;
    private LocalDateTime submittedAt;
    private String teacherFeedback;
    private String aiFeedback;
}
