package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.MissionSessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "mission_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    // When the current step began — used to backend-calculate per-decision response time.
    @Column
    private LocalDateTime currentStepStartedAt;

    // Multi-step progress.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_step_id")
    private MissionStep currentStep;

    @Column
    private Integer currentStepOrder;

    @Column
    private Boolean missionCompleteReady;

    @Column
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MissionSessionStatus status;

    // MissionSession belongs to one Mission (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // The student attempting this mission. Nullable in the DB so legacy rows still load; always set for new
    // sessions (needed to track per-student completion of shared DEFAULT missions).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    // MissionSession has many Decisions (inverse side)
    @OneToMany(mappedBy = "missionSession", fetch = FetchType.LAZY)
    private Set<Decision> decisions;

    // MissionSession has one Insight (inverse side)
    @OneToOne(mappedBy = "missionSession", fetch = FetchType.LAZY)
    private Insight insight;
}
