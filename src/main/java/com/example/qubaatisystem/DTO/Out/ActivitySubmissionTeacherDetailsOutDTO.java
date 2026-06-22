package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Teacher-facing detailed view of one submission: the submission summary plus every answer with its earned
 * points, status, feedback, question text and the correct answer. NOT student-safe (teacher endpoint only).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySubmissionTeacherDetailsOutDTO {

    private Integer submissionId;
    private Integer activityId;
    private String activityTitle;
    private Integer studentId;
    private String studentName;
    private ActivitySubmissionStatus status;
    private Integer score;
    private Integer activityMaxScore;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private String teacherFeedback;
    private String aiFeedback;
    private List<TeacherAnswerRowOutDTO> answers;
}
