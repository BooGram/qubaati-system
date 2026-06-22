package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Optional reason the student wants a generated mission replaced. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionRegenerateInDTO {

    @Size(max = 1000, message = "reason must be at most 1000 characters")
    private String reason;
}
