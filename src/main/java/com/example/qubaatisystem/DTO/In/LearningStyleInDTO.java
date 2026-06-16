package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.LearningStyleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningStyleInDTO {

    @NotNull(message = "primaryStyle is required")
    private LearningStyleType primaryStyle;

    private LearningStyleType secondaryStyle;

    @PositiveOrZero(message = "confidence must be zero or positive")
    private Double confidence;

    @NotNull(message = "studentId is required")
    private Integer studentId;
}
