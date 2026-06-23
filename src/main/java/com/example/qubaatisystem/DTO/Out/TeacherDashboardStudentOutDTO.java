package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One student summary row in the teacher dashboard (display-safe; no credentials, no correct answers). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardStudentOutDTO {

    private Integer studentId;
    private String fullName;
    private String grade;
    private Integer age;
    private Integer totalPoints;
    private Integer completedMissionsCount;
    private Integer classroomId;
    private String classroomName;
}
