package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.MissionSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionSessionOutDTO {

    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer score;
    private MissionSessionStatus status;
    private Integer missionId;
    private String missionTitle;
}
