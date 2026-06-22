package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardOutDTO {

    private Integer teacherId;
    private String fullName;
    private String specialization;
    private String email;

    private Integer classroomCount;
    private Integer totalStudentCount;

    // Student 1: classroom + student detail.
    private List<TeacherDashboardClassroomOutDTO> classrooms;
    private List<TeacherDashboardStudentOutDTO> students;

    // Student 2 (activities) + Student 3 (missions) integrated, read-only summaries.
    private TeacherDashboardActivitySummaryOutDTO activitySummary;
    private TeacherDashboardMissionSummaryOutDTO missionSummary;
}
