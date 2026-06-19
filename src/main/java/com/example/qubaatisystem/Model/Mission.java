package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.SkillType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "mission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String scenario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillType skillType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @Column
    private Integer estimatedMinutes;

    @Column
    private Integer maxScore;

    // Mission belongs to one CareerWorld (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_world_id", nullable = false)
    private CareerWorld careerWorld;

    // AI-generated Mission can be personalized for one Student (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_for_student_id")
    private Student generatedForStudent;

    // AI can choose or create the Skill that this Mission targets (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    // Mission has many MissionSessions (inverse side)
    @OneToMany(mappedBy = "mission", fetch = FetchType.LAZY)
    private Set<MissionSession> missionSessions;

    // Mission has many choices (inverse side)
    @OneToMany(mappedBy = "mission", fetch = FetchType.LAZY)
    private Set<MissionChoice> missionChoices;
}
