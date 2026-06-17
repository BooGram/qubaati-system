package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.LearningStyleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "learning_style")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LearningStyleType primaryStyle;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LearningStyleType secondaryStyle;

    @Column
    private Double confidence;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime detectedAt;

    // LearningStyle belongs to one Student (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
}
