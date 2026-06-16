package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.MissionSessionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionSessionInDTO {

    @NotNull(message = "startTime is required")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @PositiveOrZero(message = "score must be zero or positive")
    private Integer score;

    @NotNull(message = "status is required")
    private MissionSessionStatus status;

    @NotNull(message = "missionId is required")
    private Integer missionId;
}
