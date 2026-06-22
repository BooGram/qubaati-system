package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/** One step of a multi-step mission: an ordered scenario whose choices may branch to a next step. */
@Entity
@Table(name = "mission_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false)
    private Integer stepOrder;

    @Column(columnDefinition = "TEXT")
    private String scenario;

    @Column
    private Boolean finalStep;

    // MissionStep has many choices (inverse side)
    @OneToMany(mappedBy = "missionStep", fetch = FetchType.LAZY)
    private Set<MissionChoice> choices;
}
