package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Body for teacher "assign activity to student". activityId + studentId are resource targets; the assigning
 *  teacher is derived from Basic Auth (no assignedByTeacher in the body). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignStudentInDTO {

    @NotNull(message = "activityId is required")
    private Integer activityId;

    @NotNull(message = "studentId is required")
    private Integer studentId;

    private LocalDateTime dueDate;
}
