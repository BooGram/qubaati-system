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

    // ----- User account fields (used to create the linked User internally) -----
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

    @Size(max = 30, message = "parentPhoneNumber must be at most 30 characters")
    private String parentPhoneNumber;

    @Email(message = "parentEmail must be a valid email")
    private String parentEmail;

    private Integer classroomId;
}
