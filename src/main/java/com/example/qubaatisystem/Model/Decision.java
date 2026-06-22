package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "decision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Decision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String choice;

    @Column(length = 1000)
    private String reason;

    @Column
    private Boolean isCorrect;

    @Column
    private Double responseTimeSeconds;

    // Internal scoring of the chosen option (never exposed to the student).
    @Column
    private Integer scoreImpact;

    @Column
    private LocalDateTime submittedAt;

    // Decision belongs to one MissionSession (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_session_id", nullable = false)
    private MissionSession missionSession;

    // The step this decision answered (multi-step missions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_step_id")
    private MissionStep missionStep;
}
