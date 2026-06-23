package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body carrying a target mission id (path-variable-free style). The actor comes from Basic Auth. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionTargetInDTO {

    @NotNull(message = "missionId is required")
    private Integer missionId;
}
