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

    @Column
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MissionSessionStatus status;

    // MissionSession belongs to one Mission (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // MissionSession has many Decisions (inverse side)
    @OneToMany(mappedBy = "missionSession", fetch = FetchType.LAZY)
    private Set<Decision> decisions;

    // MissionSession has one Insight (inverse side)
    @OneToOne(mappedBy = "missionSession", fetch = FetchType.LAZY)
    private Insight insight;
}
