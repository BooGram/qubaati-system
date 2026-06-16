package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomOutDTO {

    private Integer id;
    private String name;
    private Integer gradeLevel;
    private String section;
    private Integer teacherId;
    private String teacherName;
}
