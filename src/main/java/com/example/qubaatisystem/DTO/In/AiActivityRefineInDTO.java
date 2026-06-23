package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body for the body-based AI refine endpoint. activityId is the target; the teacher comes from Basic Auth. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiActivityRefineInDTO {

    @NotNull(message = "activityId is required")
    private Integer activityId;

    @Size(max = 2000, message = "instruction must be at most 2000 characters")
    private String instruction;
}
