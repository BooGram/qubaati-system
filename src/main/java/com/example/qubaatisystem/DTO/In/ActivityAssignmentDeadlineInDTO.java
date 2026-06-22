package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAssignmentDeadlineInDTO {

    // Resource target: the assignment whose deadline is extended. Moved into the body from the former path variable.
    @NotNull(message = "assignmentId is required")
    private Integer assignmentId;

    @NotNull(message = "dueDate is required")
    @Future(message = "dueDate must be in the future")
    private LocalDateTime dueDate;
}
