package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "career_world")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CareerWorld {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 80)
    private String category;

    @Column(length = 1000)
    private String description;

    // CareerWorld has many Missions (inverse side)
    @OneToMany(mappedBy = "careerWorld", fetch = FetchType.LAZY)
    private List<Mission> missions = new ArrayList<>();
}
