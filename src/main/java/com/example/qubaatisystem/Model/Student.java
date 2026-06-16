package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 50)
    private String grade;

    @Column(nullable = false)
    private Integer totalPoints;

    @Column(nullable = false)
    private Integer completedMissionsCount;

    @Column(length = 30)
    private String parentPhoneNumber;

    @Column(length = 120)
    private String parentEmail;

    // Student belongs to one User (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Student belongs to one Classroom (owning side, optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    // Student has many StudentSkills (inverse side)
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<StudentSkill> studentSkills = new ArrayList<>();

    // Student has one LearningStyle (inverse side)
    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY)
    private LearningStyle learningStyle;
}
