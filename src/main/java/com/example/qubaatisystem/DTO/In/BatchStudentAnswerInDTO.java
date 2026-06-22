package com.example.qubaatisystem.DTO.In;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchStudentAnswerInDTO {

    // Resource target: the submission these answers belong to. Moved into the body from the former path variable.
    @NotNull(message = "submissionId is required")
    private Integer submissionId;

    @NotEmpty(message = "answers must not be null or empty")
    @Valid
    private List<SingleStudentAnswerInDTO> answers;
}
