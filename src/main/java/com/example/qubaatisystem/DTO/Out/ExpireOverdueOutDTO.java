package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Result of the expire-overdue-assignments automation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpireOverdueOutDTO {

    private Integer expiredCount;
}
