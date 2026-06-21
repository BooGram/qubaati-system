package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.MissionSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row in the available-missions list. Never exposes choice scoring. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableMissionOutDTO {

    private Integer id;
    private String title;
    private MissionSource source;
    private Integer generationSlot;
    private Boolean completed;
    private Boolean active;
}
