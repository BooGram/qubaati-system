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

    // MissionChoice belongs to one Mission (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
}
