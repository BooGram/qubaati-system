package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomInDTO {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotNull(message = "gradeLevel is required")
    @Positive(message = "gradeLevel must be positive")
    private Integer gradeLevel;

    @Size(max = 20, message = "section must be at most 20 characters")
    private String section;

    @NotNull(message = "teacherId is required")
    private Integer teacherId;
}
