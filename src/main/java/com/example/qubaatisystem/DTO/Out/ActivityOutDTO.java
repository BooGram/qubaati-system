package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Enum.ActivityType;
import com.example.qubaatisystem.Enum.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOutDTO {

    private Integer id;
    private String title;
    private String description;
    private ActivityType type;
    private ActivityStatus status;
    private DifficultyLevel difficulty;
    private Integer maxScore;
    private java.time.LocalDateTime createdAt;

    // Teacher ownership (Student 1). Null for legacy/unowned activities.
    private Integer createdByTeacherId;
    private String createdByTeacherName;

    // Target skill (used by grading analytics). Null when the activity has no mapped skill.
    private Integer skillId;
    private String skillName;
}
