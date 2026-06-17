package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentOutDTO {

    private Integer id;

    // ----- Safe linked User fields (never the password) -----
    private Integer userId;
    private String username;
    private String email;
    private UserRole role;

    // ----- Student profile fields -----
    private String fullName;
    private Integer age;
    private String grade;
    private Integer totalPoints;
    private Integer completedMissionsCount;

    private Integer classroomId;
    private String classroomName;

    // ----- Safe Parent information (from the Parent and the Parent's linked User) -----
    private Integer parentId;
    private String parentName;
    private String parentEmail;
    private String parentPhoneNumber;
}
