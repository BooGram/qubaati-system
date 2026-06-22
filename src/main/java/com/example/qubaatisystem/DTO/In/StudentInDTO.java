package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInDTO {

    // Resource target id, used only for body-based update (null on create).
    private Integer id;

    // ----- User account fields (the linked child User is created internally by StudentService) -----
    @NotBlank(message = "username is required")
    @Size(max = 50, message = "username must be at most 50 characters")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password must be between 6 and 100 characters")
    private String password;

    // ----- Student profile fields -----
    @NotBlank(message = "fullName is required")
    @Size(max = 120, message = "fullName must be at most 120 characters")
    private String fullName;

    @NotNull(message = "age is required")
    @Min(value = 3, message = "age must be at least 3")
    @Max(value = 100, message = "age must be at most 100")
    private Integer age;

    @NotBlank(message = "grade is required")
    @Size(max = 50, message = "grade must be at most 50 characters")
    private String grade;

    @PositiveOrZero(message = "totalPoints must be zero or positive")
    private Integer totalPoints;

    @PositiveOrZero(message = "completedMissionsCount must be zero or positive")
    private Integer completedMissionsCount;

    @NotNull(message = "parentId is required")
    private Integer parentId;

    private Integer classroomId;
}
