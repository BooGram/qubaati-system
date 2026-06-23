package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Result of the due-soon-notifications automation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DueSoonNotificationsOutDTO {

    private Integer notifiedCount;
}
