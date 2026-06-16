package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionInDTO {

    @NotBlank(message = "content is required")
    @Size(max = 500, message = "content must be at most 500 characters")
    private String content;

    @NotNull(message = "isCorrect is required")
    private Boolean isCorrect;

    @NotNull(message = "questionId is required")
    private Integer questionId;
}
