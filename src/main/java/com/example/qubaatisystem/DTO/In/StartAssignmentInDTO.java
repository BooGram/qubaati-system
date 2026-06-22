package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body for student "start assignment". The student is derived from Basic Auth (no studentId). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartAssignmentInDTO {

    @NotNull(message = "assignmentId is required")
    private Integer assignmentId;
}
