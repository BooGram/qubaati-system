package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionType type;

    @Column
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @Column(length = 1000)
    private String correctAnswer;

    // Question belongs to one Activity (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // Question has many Options (inverse side)
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    // Question has many StudentAnswers (inverse side)
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<StudentAnswer> studentAnswers = new ArrayList<>();
}
