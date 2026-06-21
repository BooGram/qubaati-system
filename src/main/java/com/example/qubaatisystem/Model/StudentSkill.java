package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "student_skill", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_skill_student_skill", columnNames = {"student_id", "skill_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Double score;

    @Column(name = "skill_level")
    private Integer level;

    @UpdateTimestamp
    @Column(nullable = false)
    private java.time.LocalDateTime lastUpdated;

    // StudentSkill belongs to one Student (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // StudentSkill belongs to one Skill (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
