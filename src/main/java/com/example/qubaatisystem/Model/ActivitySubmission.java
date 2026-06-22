package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "activity_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private java.time.LocalDateTime startedAt;

    @Column
    private java.time.LocalDateTime submittedAt;

    @Column
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActivitySubmissionStatus status;

    @Column(length = 2000)
    private String aiFeedback;

    // Provenance of aiFeedback so the client never mistakes a template for real AI: "AI" = Spring AI ChatClient,
    // "SYSTEM" = deterministic score summary used when no AI provider is configured. Null for manual/teacher grades.
    private String feedbackSource;

    @Column(length = 2000)
    private String teacherFeedback;

    // How many activity points this submission has already contributed to Student.totalPoints. Used for safe
    // delta accounting so re-grading/returning/reopening never double-counts (null == 0 applied so far).
    @Column
    private Integer pointsAppliedToStudentTotal;

    // ActivitySubmission belongs to one ActivityAssignment (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_assignment_id", nullable = false)
    private ActivityAssignment activityAssignment;

    // ActivitySubmission belongs to one Student (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // ActivitySubmission has many StudentAnswers (inverse side)
    @OneToMany(mappedBy = "activitySubmission", fetch = FetchType.LAZY)
    private Set<StudentAnswer> studentAnswers;
}
