package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body for the legacy generate-mission-for-student endpoint (path-variable-free style). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMissionInDTO {

    @NotNull(message = "studentId is required")
    private Integer studentId;

    @NotNull(message = "worldId is required")
    private Integer worldId;
}
