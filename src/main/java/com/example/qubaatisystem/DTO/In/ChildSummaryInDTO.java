package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body referencing a child (student) by id for the body-based AI child-summary endpoint. The parent is derived
 *  from Basic Auth and must own the child. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildSummaryInDTO {

    @NotNull(message = "studentId is required")
    private Integer studentId;
}
