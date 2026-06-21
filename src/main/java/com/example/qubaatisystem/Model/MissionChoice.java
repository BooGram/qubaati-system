package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mission_choice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 10)
    private String choiceKey;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    private Integer scoreImpact;

    // Optional branch target: the stepOrder to jump to when this choice is selected (null => next-in-sequence).
    @Column
    private Integer nextStepOrder;

    // MissionChoice belongs to one Mission (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // MissionChoice belongs to one MissionStep (nullable for legacy single-step missions until migrated)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_step_id")
    private MissionStep missionStep;
}
