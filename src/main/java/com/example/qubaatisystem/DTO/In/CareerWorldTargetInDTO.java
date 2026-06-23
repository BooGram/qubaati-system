package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body carrying a target career-world id (path-variable-free style). The actor comes from Basic Auth. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerWorldTargetInDTO {

    @NotNull(message = "careerWorldId is required")
    private Integer careerWorldId;
}
