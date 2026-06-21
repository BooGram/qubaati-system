package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.ActivityAssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** One assignment row in the student activity dashboard (what the student needs to do / what is due soon). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardAssignmentOutDTO {

    private Integer assignmentId;
    private Integer activityId;
    private String activityTitle;
    private LocalDateTime dueDate;
    private ActivityAssignmentStatus status;
}
