package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body for teacher classroom enroll/remove. classroomId + studentId are resource targets; the acting teacher
 *  comes from Basic Auth and must own the classroom. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollStudentInDTO {

    @NotNull(message = "classroomId is required")
    private Integer classroomId;

    @NotNull(message = "studentId is required")
    private Integer studentId;
}
