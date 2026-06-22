package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body carrying a target student id (path-variable-free style). The actor comes from Basic Auth. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentTargetInDTO {

    @NotNull(message = "studentId is required")
    private Integer studentId;
}
