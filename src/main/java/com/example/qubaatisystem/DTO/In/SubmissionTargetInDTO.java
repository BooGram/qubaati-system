package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body referencing a submission by id for the body-based submit/result endpoints. The student is derived from
 *  Basic Auth and must own the submission. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionTargetInDTO {

    @NotNull(message = "submissionId is required")
    private Integer submissionId;
}
