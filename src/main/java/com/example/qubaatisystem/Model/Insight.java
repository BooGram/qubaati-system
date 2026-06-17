package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "insight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer focusScore;

    @Column
    private Integer engagementScore;

    @Column
    private Integer reasoningScore;

    @Column
    private Integer problemSolvingScore;

    @Column
    private Integer decisionMakingScore;

    @Column(length = 2000)
    private String summary;

    @Column(length = 2000)
    private String recommendation;

    // Insight belongs to one MissionSession (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_session_id", nullable = false, unique = true)
    private MissionSession missionSession;
}
