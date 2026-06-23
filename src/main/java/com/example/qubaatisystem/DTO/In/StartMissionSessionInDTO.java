package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body for starting a mission session. The student is derived from Basic Auth; studentId is OPTIONAL and only
 * honored for an ADMIN acting on a student's behalf.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartMissionSessionInDTO {

    @NotNull(message = "missionId is required")
    private Integer missionId;

    // Optional: only an admin may target another student; otherwise ignored (the actor's own id is used).
    private Integer studentId;
}
