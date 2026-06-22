package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Student activity dashboard: counts of work by state, the average graded score, the latest feedback, and the
 * lists the frontend needs (due-soon assignments, returned submissions, recent graded results). No correct
 * answers are ever exposed here.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivityDashboardOutDTO {

    private Integer studentId;

    private Integer assignedCount;
    private Integer inProgressCount;
    private Integer submittedCount;
    private Integer gradedCount;
    private Integer returnedCount;
    private Integer overdueCount;
    private Integer dueSoonCount;

    private Double averageScore;
    private String latestFeedback;

    private List<StudentDashboardAssignmentOutDTO> dueSoonAssignments;
    private List<StudentDashboardSubmissionOutDTO> returnedSubmissions;
    private List<StudentDashboardSubmissionOutDTO> recentGradedSubmissions;
}
