package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.LearningStyleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningStyleOutDTO {

    private Integer id;
    private LearningStyleType primaryStyle;
    private LearningStyleType secondaryStyle;
    private Double confidence;
    private java.time.LocalDateTime detectedAt;
    private Integer studentId;
}
