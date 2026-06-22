package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body referencing a classroom by id for the body-based AI classroom-summary endpoint. The teacher is derived
 *  from Basic Auth and must own the classroom. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomSummaryInDTO {

    @NotNull(message = "classroomId is required")
    private Integer classroomId;
}
