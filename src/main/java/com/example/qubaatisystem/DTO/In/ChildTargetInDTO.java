package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body for parent child-selection actions (overview / learning-profile) in the body-based,
 * path-variable-free controller style. {@code studentId} is the target child; the acting parent
 * comes from Basic Auth.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildTargetInDTO {

    @NotNull(message = "studentId is required")
    private Integer studentId;
}
