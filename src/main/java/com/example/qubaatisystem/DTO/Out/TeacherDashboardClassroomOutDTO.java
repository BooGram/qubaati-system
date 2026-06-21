package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One classroom row in the teacher dashboard. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDashboardClassroomOutDTO {

    private Integer classroomId;
    private String name;
    private Integer gradeLevel;
    private String section;
    private Integer studentCount;
}
