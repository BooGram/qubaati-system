package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.SkillType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillType skillType;

    // Skill has many StudentSkills (inverse side)
    @OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
    private List<StudentSkill> studentSkills = new ArrayList<>();
}
